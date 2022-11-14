package com.mytest.composetest.friend.data

/**
 * 친구 리스트에서 header를 표현하기 위해서 사용
 * @param title header 문구
 * @param isExpandable expand / collapse 지원 여부
 * @param isExpanded 현재 확장/ 접기 상태 표기
 */
data class FriendUiHeader(
    val title: String,
    val isExpandable: Boolean = false,
    val isExpanded: Boolean = true,
    override val uiId: Long, //일반 친구 목록이 아닌 경우 FriendListUiType의 offset을 이용하여 음수로 만들어 사용한다.
    override val uiType: FriendListUiType = FriendListUiType.Header()
) : FriendUiModel

