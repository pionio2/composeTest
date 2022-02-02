package com.mytest.composetest.billing.ui

import android.app.Activity
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mytest.composetest.ComposeTestApplication
import com.mytest.composetest.billing.BillingManager
import com.mytest.composetest.billing.ProductType
import com.mytest.composetest.billing.SkuInfo
import com.mytest.composetest.util.LogDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BillingTestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel(), DefaultLifecycleObserver {
    companion object {
        private const val TAG = "BillingTestViewModel"
    }

    private var consumableSKUs = listOf<String>()
    private var unConsumableSKUs = listOf<String>()
    private var subscriptionSKUs = listOf<String>()

    private val _billingManager = MutableStateFlow<BillingManager?>(null)

    // 상품 목록
    private val _skuInfoList = MutableStateFlow<List<SkuInfo>>(listOf())
    val skuInfoList = _skuInfoList.asStateFlow()

    // 스낵바 메시지. (5개 메시지 까지는 keep해 놓고 차례로 보여준다.
    private val _snackbarMessage = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 5)
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            val consumableDeferred = async(Dispatchers.IO) { getConsumableProducts() }
            val unConsumableDeferred = async(Dispatchers.IO) { getUnConsumableProducts() }
            val subscriptionDeferred = async(Dispatchers.IO) { getSubscriptionProducts() }

            consumableSKUs = consumableDeferred.await()
            unConsumableSKUs = unConsumableDeferred.await()
            subscriptionSKUs = subscriptionDeferred.await()

            _billingManager.value = BillingManager(
                ComposeTestApplication.getInstance(),
                ComposeTestApplication.getInstance(),
                consumableSKUs,
                unConsumableSKUs,
                subscriptionSKUs
            )

            registerCollector()
        }

        // 10초 후에 추가적으로 소비성 상품 한개를 추가한다. (TEST 목적)
        viewModelScope.launch {
            delay(10000)
            _billingManager.value?.changeSkuList(listOf("theme_5_charge", "theme_1_charge"), ProductType.CONSUMABLE)
        }
    }

    private suspend fun registerCollector() {
        // BillingManager에서 변경되는 상품 정보를 relay 해준다.
        _billingManager.collect { billingManager ->
            billingManager?.skuInfoList?.collect {
                _skuInfoList.value = it
            }
        }
    }

    suspend fun onPurchaseClicked(activity: Activity, skuInfo: SkuInfo) {
        when (skuInfo.skuState) {
            SkuInfo.SkuState.SKU_STATE_PURCHASED,
            SkuInfo.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED -> _snackbarMessage.emit(SnackbarMessage("이미 구매가 완료된 상품입니다."))
            SkuInfo.SkuState.SKU_STATE_PENDING -> _snackbarMessage.emit(SnackbarMessage("결재수단에 문제가 있습니다. PlayStore에서 결재 정보를 확인해 주세요."))
            SkuInfo.SkuState.SKU_STATE_UNPURCHASED -> {
                _billingManager.value?.launchBillingFlow(activity, skuInfo.sku)
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        LogDebug(TAG) { "onResume()" }
        // 스낵바 값 초기화.
        viewModelScope.launch {
            _snackbarMessage.emit(SnackbarMessage())
            _billingManager.value?.refreshAllPurchases()
        }
    }

    /**
     * 소비성 상품 리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getConsumableProducts(): List<String> {
        delay(100)
//        return mutableListOf("theme_5_charge", "theme_1_charge")
        return listOf("theme_5_charge")
    }

    /**
     * 비소비성 상품 리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getUnConsumableProducts(): List<String> {
        delay(100)
        return listOf("remove_ads")
    }

    /**
     * 구독독 상품리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getSubscriptionProducts(): List<String> {
        delay(100)
        return listOf("premium_1_month", "basic_1_month")
    }
}


