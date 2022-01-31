package com.mytest.composetest.billing

import com.android.billingclient.api.SkuDetails

// 상품(Sku)의 구매상태 & 상세 정보
data class SkuInfo(val skuState: SkuState, val sku: String, val skuDetails: SkuDetails?) {
    enum class SkuState {
        SKU_STATE_UNPURCHASED, //구매 안됨.
        SKU_STATE_PENDING, // 구매 대기중 (결재 문제거나, 여타 문제로 결재 완료가 안떨어진 상태)
        SKU_STATE_PURCHASED, // 구매됨.
        SKU_STATE_PURCHASED_AND_ACKNOWLEDGED // 구매 완료 및 google play에 인증까지 완료.
    }
}
