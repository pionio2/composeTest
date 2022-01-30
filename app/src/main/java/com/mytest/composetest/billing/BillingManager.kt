package com.mytest.composetest.billing

import android.app.Activity
import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.android.billingclient.api.*
import com.mytest.composetest.util.LogDebug
import com.mytest.composetest.util.LogError
import com.mytest.composetest.util.LogInfo
import com.mytest.composetest.util.LogWarn
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.mytest.composetest.billing.SkuInfo.SkuState
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.min

/**
 * 인앱 결제를 위한 모듈
 * @param appContext : application scope
 * @param appCoroutineScope : GlobalScope
 *
 * 기본 시작 절차
 * 1. billingClient 생성
 * 2. billing service에 connect
 * 3. 상품 detail 정보 갱시
 * 4. 혹시나 구매확인이 (ack or consume)이 안된 상품이 있는지 확인
 *
 * 구매 절자
 * 1. billing flow 시작
 * 2. 완료시 billingConnectionFlow가 trigger 되면서 상품 상세쿼리, 구매 확정 refresh
작*/
@OptIn(ExperimentalCoroutinesApi::class)
class BillingManager(
    private val appContext: Context,
    private val consumableSKUs: List<String>, // 소비성 상품 목룍
    private val unConsumableSKUs: List<String>, // 비소비성 상품 목룍
    private val subscriptionsSKUs: List<String>, // 구독 상품 목록
    private val appCoroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "BillingManager"
        private const val RECONNECT_START_TIME_IN_MILLS = 1L * 1000L //1초
        private const val RECONNECT_MAX_TIME_IN_MILLS = 1000L * 60L * 15L // 15 minutes
        const val PURCHASE_INIT_STATE = -999
    }

    enum class ProductType(val typeString: String) {
        CONSUMABLE(BillingClient.SkuType.INAPP), // 소모성
        UNCONSUMABLE(BillingClient.SkuType.INAPP), // 비소모성
        SUBSCRIPTION(BillingClient.SkuType.SUBS), // 구독형
    }

    private val billingClient: BillingClient

    // Billing service에 connection 성공 / 실패시 trigger 된다. (내부적으로 상품 정보 갱신 및 재시도를 위해 사용된다.)
    private val billingConnectionFlow = MutableSharedFlow<Boolean>()

    // Billing launching의 구매 성공 / 실패에 대한 응답 전송
    private val _purchaseResult = MutableStateFlow<Int>(PURCHASE_INIT_STATE)
    private val purchaseResult = _purchaseResult.asStateFlow()

    // Billing service에 connection 재시도 호출
    private var reconnectTime = RECONNECT_START_TIME_IN_MILLS

    // 상품별 정보 flow
    private val _skuInfoList = MutableStateFlow<List<SkuInfo>>(listOf())
    val skuInfoList = _skuInfoList.asStateFlow()
    private val skuInfoListCalculateDispatcher = Dispatchers.Default.limitedParallelism(1)

    // 확정 처리중인 소비성 물품 리스트
    private val purchaseConsumptionInProcess: MutableSet<Purchase> = HashSet()


    init {
        //1. 초기 skuInfo 생성
        val allSkuList = mutableListOf<String>().apply {
            addAll(consumableSKUs)
            addAll(unConsumableSKUs)
            addAll(subscriptionsSKUs)
        }
        _skuInfoList.value = allSkuList.map { SkuInfo(SkuState.SKU_STATE_UNPURCHASED, it, null) }

        //2. billing client 생성
        val purchaseCallback = PurchasesUpdatedListener { billingResult, list -> appCoroutineScope.launch { onPurchasesUpdated(billingResult, list) } }
        billingClient = BillingClient.newBuilder(appContext)
            .setListener(purchaseCallback) // 구매 결재가 완료되면 호출될 콜백
            .enablePendingPurchases() //대기중이 거래를 지원
            .build()

        //3. 각종 flow에 대한 collect 등록
        collectStates()

        //4. connect billing service
        appCoroutineScope.launch { connectBillingService() }
    }

    // connect billing service
    private suspend fun connectBillingService() {
        val billingConnCallback = object : BillingClientStateListener {
            // billing service에 connection 실패 -> 재시도 호출
            override fun onBillingServiceDisconnected() {
                LogError(TAG) { "onBillingServiceDisconnected()" }
                appCoroutineScope.launch { billingConnectionFlow.emit(false) }
            }

            // billing service에 connection 성공
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                LogDebug(TAG) { "onBillingSetupFinished()" }
                appCoroutineScope.launch { billingConnectionFlow.emit(billingResult.responseCode == BillingClient.BillingResponseCode.OK) }
            }
        }
        billingClient.startConnection(billingConnCallback)
    }

    private fun collectStates() {
        // Billing service connection에 대한 응답이 오면 호출된다.
        appCoroutineScope.launch {
            billingConnectionFlow.collect { isSuccess ->
                if (isSuccess) {
                    // 재접속 시간 초기화
                    reconnectTime = RECONNECT_START_TIME_IN_MILLS
                    // billing service 완료후 상품 상세 정보를 가져 온다.
                    // google play 찔러서 상품정보(detail) state flow에 재 반영
                    querySkuDetails(consumableSKUs, ProductType.CONSUMABLE)
                    querySkuDetails(unConsumableSKUs, ProductType.UNCONSUMABLE)
                    querySkuDetails(subscriptionsSKUs, ProductType.SUBSCRIPTION)
                    refreshAllPurchases() // 소비성, 비소비성, 구독 관련된 정보를 google play에 질러서 state flow에 재 반영.
                } else {
                    retryBillingServiceConnectionWithExponentialBackoff()
                }
            }
        }
    }

    /**
     * Google play의 구매 UX를 띄운다.
     */
    @MainThread
    suspend fun launchBillingFlow(activity: Activity, sku: String): Boolean {
        if (billingClient.isReady.not()) {
            LogError(TAG) { "billingClient is not connected!" }
            return false
        }

        //구독 상품인 경우 업그레이드를 위해 이전 토큰이 필요하다.
        val purchaseToken = if (subscriptionsSKUs.contains(sku)) {
            withContext(Dispatchers.IO) { getSubscriptionSkuToken(sku) }
        } else {
            null
        }

        return launchingBillingFlow(activity, sku, purchaseToken)
    }

    /**
     * Google play의 결재 UX를 시작한다. 따라서 수행이 진행되는 activity가 param으로 필요함.
     * 구독 업그레이드시 기존 SKU 값이 필요하다.
     *
     * @param activity active activity to launch our billing flow from
     * @param sku SKU (Product ID) to be purchased
     * @param purchaseToken 구독의 경우 업그레이드 할 sku의 token
     * @return true if launch is successful
     */
    @MainThread
    private suspend fun launchingBillingFlow(activity: Activity, sku: String, purchaseToken: String?): Boolean {
        val skuInfo = withContext(skuInfoListCalculateDispatcher) { _skuInfoList.value.find { it.sku == sku } }
        if (skuInfo?.skuDetails == null) {
            LogError(TAG) { "launchBillingFlow() - sku:$sku has not details." }
            return false
        }

        val billingFlowParamsBuilder = BillingFlowParams.newBuilder().apply {
            setSkuDetails(skuInfo.skuDetails)
            if (purchaseToken != null) {
                setSubscriptionUpdateParams(
                    BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                        .setOldSkuPurchaseToken(purchaseToken)
                        .build()
                )
            }
        }

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParamsBuilder.build())
        return billingResult.responseCode == BillingClient.BillingResponseCode.OK
    }

    @MainThread
    fun disconnectBillingService() {
        LogDebug(TAG) { "disconnectBillingService()" }
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    /**
     * 구매된 상품(소모, 비소모, 구독)정보를 google play에 찔러서 받아오고
     * onResume(), onCreate()에서 호출해야한다.
     */
    @AnyThread
    suspend fun refreshAllPurchases() {
        LogDebug(TAG) { "refreshPurchases() - Refreshing purchases info and final confirm" }
        if (billingClient.isReady.not()) {
            LogError(TAG) { "billingClient is not connected!" }
            return
        }
        coroutineScope {
            launch(Dispatchers.IO) { refreshPurchases(ProductType.CONSUMABLE, consumableSKUs) }
            launch(Dispatchers.IO) { refreshPurchases(ProductType.UNCONSUMABLE, unConsumableSKUs) }
            launch(Dispatchers.IO) { refreshPurchases(ProductType.SUBSCRIPTION, subscriptionsSKUs) }
        }
        LogDebug(TAG) { "refreshPurchases() - Refreshing purchases finished." }
    }

    // 구매 상품 정보를 얻어 온다.
    @WorkerThread
    private suspend fun refreshPurchases(productType: ProductType, skuList: List<String>) {
        LogDebug(TAG) { "refreshPurchases() - productType: $productType" }
        val purchasesResult = billingClient.queryPurchasesAsync(productType.typeString)
        val billingResult = purchasesResult.billingResult
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            LogError(TAG) { "refreshPurchases() - Problem getting $productType: ${billingResult.debugMessage}" }
        } else {
            confirmPurchaseToGooglePlay(purchasesResult.purchasesList, skuList)
        }
    }

    /**
     * billing client의 callback으로 laucnBillingFlow가 완료되면 (결제가 완료되면) 호출된다.
     * @param billingResult result of the purchase flow.
     * @param list of new purchases.
     */
    private suspend fun onPurchasesUpdated(billingResult: BillingResult, list: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (list.isNullOrEmpty()) {
                    LogDebug(TAG) { "onPurchasesUpdated() - purchase list is null or Empty" }
                } else {
                    confirmPurchaseToGooglePlay(list, null)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> LogWarn(TAG) { "onPurchasesUpdated() - User canceled the purchase" }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> LogInfo(TAG) { "onPurchasesUpdated() - The user already owns this item" }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> LogError(TAG) { "onPurchasesUpdated() - Google Play does not recognize the configuration" }
            else -> LogDebug(TAG) { "onPurchasesUpdated() - BillingResult [${billingResult.responseCode}]: ${billingResult.debugMessage}" }
        }

        _purchaseResult.value = billingResult.responseCode
        _purchaseResult.value = PURCHASE_INIT_STATE
    }

    /**
     * billing service connection 오류 발생시 재시도
     * backoff algorithm으로 2의 배수 분 가격으로 시도한다. (최대 간격 15분)
     * ex) 1분-> 2분 -> 4분 -> 8분 -> 15분 -> 15분 -> 15분....
     */
    private fun retryBillingServiceConnectionWithExponentialBackoff() {
        LogWarn(TAG) { "retryBillingServiceConnectionWithExponentialBackoff() - backoff time: $reconnectTime" }
        appCoroutineScope.launch {
            delay(reconnectTime)
            connectBillingService()
            reconnectTime = min(reconnectTime * 2, RECONNECT_MAX_TIME_IN_MILLS)
        }
    }

    /**
     * Google play로 부터 구매 목록을 받아서 구매 확정 및 현재 가지고 았는 목록의 구매 상태를 업데이트 한다.
     *
     * 구매한 상품의 signature 확인고와 acknowledges 후에 PUCHASED로 상태가 바뀐다.
     * acknowledge는 서버에서  Google Play Developer API로 처리하는게 더 신뢰성이 있다.
     * 만약 3일이내에 acknowledge를 주지 않으면 google play가 환불 및 구매처리를 원복 시킨다.
     *
     * 두번째 param인 skusToUpdate 목록이 전달되는 경우 목록에 없는 모든 구매 상태는 UNPURCHASED로 변경함.
     *
     * @param purchases the List of purchases to process.
     * @param skusToUpdate a list of skus that we want to update the state from --- this allows us
     * to set the state of non-returned SKUs to UNPURCHASED.
     */
    @WorkerThread
    private suspend fun confirmPurchaseToGooglePlay(purchases: List<Purchase>?, skusToUpdate: List<String>?) {
        if (purchases.isNullOrEmpty()) {
            LogInfo(TAG) { "processPurchaseList() - there is no purchases" }
            // update 요청 목록에 있는 모든 상품은 미구매 상태로 바꾼다.
            skusToUpdate?.forEach { setSkuState(it, SkuState.SKU_STATE_UNPURCHASED) }
            return
        }

        for (purchase in purchases) {
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    // 정합성 체크
                    if (isSignatureValid(purchase)) {
                        setSkuStateFromPurchase(purchase)
                        val consumableSkuMap = purchase.skus.groupBy { consumableSKUs.contains(it) }
                        val consumableCount = consumableSkuMap[true]?.size ?: 0 //소비성 물품 개수
                        val unConsumableCount = consumableSkuMap[false]?.size ?: 0 //비소비성 or 구독 물품 개수

                        // 물품 구매 확정을 google play에 요청한다.
                        when {
                            consumableCount > 0 && unConsumableCount > 0 -> { // 소비성 / 비소비성 물품이 같이 존재하면 안된다.!!
                                LogError(TAG) { "processPurchaseList() - Purchase cannot contain a mixture of consumable " }
                                // 비소비성으로 처리한다.
                                acknowledgePurchase(purchase)
                            }
                            consumableCount > 0 -> { //소비성 물품 확정 처리
                                consumePurchase(purchase)
                            }
                            unConsumableCount > 0 -> { //비소비성 물품 확정 처리
                                acknowledgePurchase(purchase)
                            }
                            else -> LogError(TAG) { "processPurchaseList() - there is no sku in purchase result" }
                        }
                    } else {
                        LogError(TAG) { "Invalid signature. Check to make sure your public key is correct." }
                    }
                }
                else -> { // purchase에 포함된 sku중 구매되지 않은것들은 구매 상태 변경한다.
                    setSkuStateFromPurchase(purchase)
                }
            }

            //넘겨받은 (skusToUpdate) sku중에 google play에 구매정보가 없는 상품의 경우 미구매로 처리한다.
            skusToUpdate?.let { updateStateForNotExistSkuInPurchasedList(purchases, it) }
        }
    }

    /**
     * 해당 sku의 구매 상태 변경한다.
     * @param sku product ID to change the state of
     * @param newSkuState the new state of the sku.
     */
    @AnyThread
    private suspend fun setSkuState(sku: String, newSkuState: SkuState) {
        withContext(skuInfoListCalculateDispatcher) {
            val skuInfo = _skuInfoList.value.find { it.sku == sku }
            if (skuInfo == null) {
                LogError(TAG) { "setSkuState() - Unknown SKU $sku. Check to make sure SKU matches SKUS in the Play developer console." }
            } else {
                val newSkuInfoList = mutableListOf<SkuInfo>().apply {
                    addAll(_skuInfoList.value)
                }
                val index = newSkuInfoList.indexOf(skuInfo)
                if (index != -1) {
                    newSkuInfoList[index] = skuInfo.copy(skuState = newSkuState)
                    _skuInfoList.emit(newSkuInfoList)
                }
            }
        }
    }

    /**
     * 해당 sku의 상세 정보를 변경한다.
     * @param sku product ID to change the state of
     * @param details the new details of the sku.
     */
    @AnyThread
    private suspend fun setSkuDetails(sku: String, details: SkuDetails) {
        LogInfo(TAG) { "setSkuDetails() - sku:$sku title:${details.title}" }
        withContext(skuInfoListCalculateDispatcher) {
            val skuInfo = _skuInfoList.value.find { it.sku == sku }
            if (skuInfo == null) {
                LogError(TAG) { "setSkuDetails() - Unknown SKU $sku. Check to make sure SKU matches SKUS in the Play developer console." }
            } else {
                val newSkuInfoList = mutableListOf<SkuInfo>().apply {
                    addAll(_skuInfoList.value)
                }
                val index = newSkuInfoList.indexOf(skuInfo)
                if (index != -1) {
                    newSkuInfoList[index] = skuInfo.copy(skuDetails = details)
                    _skuInfoList.emit(newSkuInfoList)
                }
            }
        }
    }

    /**
     * 구매 목록을 받아서 현재 각각의 sku들의 구매 상태를 업데이트 한다.
     * 구매 상태인 경우 내부적으로 isAcknowledged를 이용하여 소비,비소비 물품을 구분하여 표기한다.
     * @param purchase an up-to-date object to set the state for the Sku
     */
    @AnyThread
    private suspend fun setSkuStateFromPurchase(purchase: Purchase) {
        LogDebug(TAG) { "setSkuStateFromPurchase: skus: ${purchase.skus}" }
        purchase.skus.forEach { purchaseSku ->
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PENDING -> setSkuState(purchaseSku, SkuState.SKU_STATE_PENDING)
                Purchase.PurchaseState.UNSPECIFIED_STATE -> setSkuState(purchaseSku, SkuState.SKU_STATE_UNPURCHASED)
                Purchase.PurchaseState.PURCHASED -> if (purchase.isAcknowledged) {
                    setSkuState(purchaseSku, SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                } else {
                    setSkuState(purchaseSku, SkuState.SKU_STATE_PURCHASED)
                }
                else -> LogError(TAG) { "setSkuStateFromPurchase() - Purchase in unknown state: ${purchase.purchaseState}" }
            }
        }
    }

    /**
     * TODO 서버를 통해 인증해야 한다.
     */
    private suspend fun isSignatureValid(purchase: Purchase): Boolean {
        delay(500L)
        return true
    }

    /**
     * signature check가 완료된 상품에 대한 구매 처리
     * @param purchase purchase to consume
     */
    @WorkerThread
    private suspend fun consumePurchase(purchase: Purchase) {
        // 동일 상품에 대한 중복 호출 방지.
        val isPurchaseConsuming = withContext(Dispatchers.Main) {
            purchaseConsumptionInProcess.contains(purchase)
        }

        if (isPurchaseConsuming) {
            LogWarn(TAG) { "consumePurchase() - skus(${purchase.skus}) consuming is already working" }
            return
        }

        // process중 설정
        withContext(Dispatchers.Main) { purchaseConsumptionInProcess.add(purchase) }

        // 구매물품 소비 처리
        val consumePurchaseResult = billingClient.consumePurchase(
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        )

        withContext(Dispatchers.Main) { purchaseConsumptionInProcess.remove(purchase) }
        if (consumePurchaseResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // 구매 처리 완료되었으므로 각각의 sku들의 상태를 미구매로 변경 (다시 구매 가능하도록)
            purchase.skus.forEach { setSkuState(it, SkuState.SKU_STATE_UNPURCHASED) }
        } else {
            LogError(TAG) { "consumePurchase() - Error while consuming: ${consumePurchaseResult.billingResult.debugMessage}" }
        }
    }

    //비소비성 물품에 대한 확정(acknowledge) - Google play에 확정
    @WorkerThread
    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val billingResult = billingClient.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        )
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            LogError(TAG) { "acknowledgePurchase() - Failed to acknowledge purchase: ${purchase.skus}" }
        } else {
            // 구매 완료로 sku 상태를 update해준다.
            purchase.skus.forEach { setSkuState(it, SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED) }
        }
    }

    /**
     * 서버에서 받은 리스트와 로컬에서 가지고 있는 리스틑 비교하여 서버에는 없고, 로컬에만 있는 sku의 상태를 미구매로 전부 업데이트 한다.
     * @param purchase google play에서 받은 구매 리스트
     * @param knownSkuList 로컬에서 가지고 있는 상품 리스트
     */
    @AnyThread
    private suspend fun updateStateForNotExistSkuInPurchasedList(purchases: List<Purchase>, knownSkuList: List<String>) {
        if (knownSkuList.isEmpty()) {
            return
        }

        // 가지고 있는(skusToUpdate) 항목중에 업데이트 해야할 SKU를 찾는다. 가지고 있는 항목이지만 (google play의) 구매 리스트에 없는 skus들을 찾는다.
        val updateTargetSkus = withContext(skuInfoListCalculateDispatcher) {
            val targetSkus = HashSet<String>()
            purchases.flatMap { it.skus }
                .forEach { purchasedSku ->
                    val skuInfo = _skuInfoList.value.find { it.sku == purchasedSku }
                    if (skuInfo != null) {
                        targetSkus.add(purchasedSku)
                    } else {
                        LogError(TAG) { "updateStateForNotExistSkuInPurchasedList() - Unknown SKU:$purchasedSku" }
                    }
                }
            targetSkus
        }

        // 업데이트 되지 못한 (google play에서 정보를 내려받지 못한) known 상품(sku들)은 모두 미구매 상태로 변경한다.
        knownSkuList.filter { updateTargetSkus.contains(it).not() }
            .forEach { setSkuState(it, SkuState.SKU_STATE_UNPURCHASED) }
    }

    /**
     * 단일 결재와, 구독 결재 상품에 대한 정보를 가져온다. 상품구매 상태 / 상품 상세 정보를 state flow에 저장한다.
     */
    @WorkerThread
    private suspend fun querySkuDetails(skuList: List<String>, productType: ProductType) {
        if (skuList.isNotEmpty()) {
            val skuDetailsResult = billingClient.querySkuDetails(
                SkuDetailsParams.newBuilder()
                    .setType(productType.typeString)
                    .setSkusList(skuList)
                    .build()
            )

            val responseCode = skuDetailsResult.billingResult.responseCode
            val debugMessage = skuDetailsResult.billingResult.debugMessage
            val skuDetailList = skuDetailsResult.skuDetailsList

            when (responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    LogInfo(TAG) { "querySkuDetails() - responseCode:$responseCode debugMsg:$debugMessage" }
                    if (skuDetailList.isNullOrEmpty()) {
                        LogError(TAG) { "querySkuDetails() - Sku details are not exist. requested skus are not matched to Google play console" }
                    } else {
                        skuDetailList.forEach { skuDetailInfo ->
                            setSkuDetails(skuDetailInfo.sku, skuDetailInfo)
                        }
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED ->
                    LogInfo(TAG) { "querySkuDetails() - user canceled. responseCode:$responseCode debugMsg:$debugMessage" }
                else -> LogError(TAG) { "querySkuDetails() - responseCode:$responseCode debugMsg:$debugMessage" }
            }
        }
    }

    /**
     * 구독을 업그레이드 하기 위해 google play에서 token을 받아온다.
     * 구독이 변경되었을때 onPurchasesUpdated가 구독에 대한 구매 상태를 반환하지 않기 때문에 이 함수를 사용한다.
     *
     * @param skus skus to get purchase information for
     * @param skuType sku type, inapp or subscription, to get purchase information for.
     * @return purchases
     */
    @WorkerThread
    private suspend fun getSubscriptionSkuToken(sku: String): String? {
        val purchasesResult = billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS)
        val billingResult = purchasesResult.billingResult
        val purchasesList = purchasesResult.purchasesList

        return when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                var skuToken: String? = null
                for (purchase in purchasesList) {
                    if (purchase.skus.contains(sku)) {
                        skuToken = purchase.purchaseToken
                        break
                    }
                }
                skuToken
            }
            else -> {
                LogError(TAG) { "getSubscriptionPurchase() - Failed to get purchase sku:$sku errMSg: ${billingResult.debugMessage}" }
                null
            }
        }
    }
}