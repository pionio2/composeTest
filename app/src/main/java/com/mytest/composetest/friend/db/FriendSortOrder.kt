package com.mytest.composetest.friend.db

data class FriendSortOrder(
    val orderType: OrderType = OrderType.DEVICE_LANGUAGE_SETTING,
    val customOrderList: List<SortOrderType> = listOf(
        SortOrderType.HANGUL,
        SortOrderType.ENGLISH,
        SortOrderType.SPECIAL_CHARACTER
    )
) {

    // 정렬값
    enum class SortOrderType {
        HANGUL,
        ENGLISH,
        SPECIAL_CHARACTER,
    }

    enum class OrderType {
        HANGUL_ENGLISH_OTHERS,
        ENGLISH_HANGUL_OTHERS,
        DEVICE_LANGUAGE_SETTING,
        CUSTOM,
    }

    val hangulOrder: Int
    val englishOrder: Int
    val specialCharacterOrder: Int

    init {
        when (orderType) {
            OrderType.HANGUL_ENGLISH_OTHERS -> {
                hangulOrder = 0
                englishOrder = 1
                specialCharacterOrder = 2
            }
            OrderType.ENGLISH_HANGUL_OTHERS -> {
                englishOrder = 0
                hangulOrder = 1
                specialCharacterOrder = 2
            }
            OrderType.DEVICE_LANGUAGE_SETTING -> {
                englishOrder = 0
                hangulOrder = 0
                specialCharacterOrder = 1
            }
            OrderType.CUSTOM -> {
                englishOrder = getOrder(SortOrderType.ENGLISH)
                hangulOrder = getOrder(SortOrderType.HANGUL)
                specialCharacterOrder = getOrder(SortOrderType.SPECIAL_CHARACTER)
            }
        }
    }

    private fun getOrder(orderType: SortOrderType): Int {
        val order = customOrderList.indexOf(orderType)
        return if (order == -1) {
            orderType.ordinal
        } else {
            order
        }
    }
}
