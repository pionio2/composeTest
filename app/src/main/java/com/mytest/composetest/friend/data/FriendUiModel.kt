package com.mytest.composetest.friend.data

/**
 * ui에 표시 가능한 sealed interface
 * @param uiId compose lazy column에서 key로 사용하기 위한 고유값(unique)
 *             즐겨찾기와 adot 친구가 있을경우 dbId를 사용하면 중복된 dbId가 전체 친구 리스트에서도 존재하기 때무에 unique 하지 않다.
 *             db에서 가져오지 않은 가공된 item의(즐겨찾기/header/adot)의 경우 db id 기반으로 unique한 값을 만들어 넣는다.

 * @param itemType header / 즐겨찾기 / 에이닷 친구 / 그냥 친구 리스트를 구분하기 위해 사용한다.
 */
sealed interface FriendUiModel {
    val uiId: Long
    val uiType: FriendListUiType
}

/**
 * 친구 목록 list에 보여줄수 있는 ui type들
 * @param uiIdOffset lazyColumn 사용시 unique 한 key로 사용하기 위한 값.
 *                   충분히 큰 값을 이용하여 친구의 db id와 겹치지 않도록 사용한다. (실제 친구 목록 이외에 가공되어 추가된 아이템들은 minus 값으로 사용한다.
 *                   FriendItem: 실제 리스트인 FriendItem은 dbId를 그대로 사용한다. key = dbId
 *                   AdotItem: 에이닷 친구목록으로 dbId + uiIdOffset을 key로 사용한다. key = (dbId + uiIdOffset) * -1
 *                   FavoriteItem: 즐겨찾기 친구목록으로 dbId + uiIdOffset을 key로 사용한다. key = (dbId + uiIdOffset) * -1
 *                   Header: 각 아이템의 header로 헤더 순서 + uiIdOffset을 key로 사용한다. key = (dbId + uiIdOffset) * -1
 */
sealed interface FriendListUiType {
    object FriendItem : FriendListUiType
    data class AdotItem(val uiIdOffset: Long = 1000000) : FriendListUiType
    data class FavoriteItem(val uiIdOffset: Long = 2000000) : FriendListUiType
    data class Header(val uiIdOffset: Long = 3000000) : FriendListUiType
}
