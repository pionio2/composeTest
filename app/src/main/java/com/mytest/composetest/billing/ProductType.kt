package com.mytest.composetest.billing

import com.android.billingclient.api.BillingClient

enum class ProductType(val typeString: String) {
    CONSUMABLE(BillingClient.SkuType.INAPP), // 소모성
    ONE_TIME_CONSUMABLE(BillingClient.SkuType.INAPP),  // 일회성 소비 상품 목록
    SUBSCRIPTION(BillingClient.SkuType.SUBS), // 구독형
}