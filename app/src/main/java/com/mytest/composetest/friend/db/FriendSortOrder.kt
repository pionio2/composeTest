package com.mytest.composetest.friend.db

data class FriendSortOrder(
    val orderList: List<FriendOrder> = listOf(
        FriendOrder.HANGUL,
        FriendOrder.ENGLISH,
        FriendOrder.SPECIAL_CHARACTER
    )
) {
    // 정렬 순서. (기본값은 한글 -> 영어 -> 특수문자)
    enum class FriendOrder {
        HANGUL,
        ENGLISH,
        SPECIAL_CHARACTER,
    }

    fun getHangulOrder() = getOrder(FriendOrder.HANGUL)

    fun getEnglishOrder() = getOrder(FriendOrder.ENGLISH)

    fun getSpecialCharacterOrder() = getOrder(FriendOrder.SPECIAL_CHARACTER)

    private fun getOrder(orderType: FriendOrder): Int {
        val order = orderList.indexOf(orderType)
        return if (order == -1) {
            orderType.ordinal
        } else {
            order
        }
    }
}
