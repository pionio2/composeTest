package com.mytest.composetest.billing.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytest.composetest.ComposeTestApplication
import com.mytest.composetest.billing.BillingManager
import com.mytest.composetest.billing.SkuInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BillingTestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "BillingTestViewModel"
    }

    private var consumableSKUs = listOf<String>()
    private var unConsumableSKUs = listOf<String>()
    private var subscriptionSKUs = listOf<String>()

    private val _billingManager = MutableStateFlow<BillingManager?>(null)

    private val _skuInfoList = MutableStateFlow<List<SkuInfo>>(listOf())
    val skuInfoList = _skuInfoList.asStateFlow()

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
                consumableSKUs,
                unConsumableSKUs,
                subscriptionSKUs,
                ComposeTestApplication.getInstance() //TODO activity에 한정시키는 버전과, process 뜰때 버전으로 바꿔야 한다.
            )

            registerCollector()
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

    /**
     * 소비성 상품 리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getConsumableProducts(): List<String> {
        delay(100)
        return mutableListOf("theme_5_charge", "theme_1_charge")
    }

    /**
     * 비소비성 상품 리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getUnConsumableProducts(): List<String> {
        delay(100)
        return mutableListOf("remove_ads")
    }

    /**
     * 구독독 상품리스트
     * TODO: 서버에서 가지고 온다.
     */
    private suspend fun getSubscriptionProducts(): List<String> {
        delay(100)
        return mutableListOf("premium_1_month", "basic_1_month")
    }
}


