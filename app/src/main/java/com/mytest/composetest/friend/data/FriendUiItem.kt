package com.mytest.composetest.friend.data

import androidx.compose.runtime.Stable

@Stable
data class FriendUiItem(
    val dbId: Long,
    val name: String,
    val phoneNumber: String,
    val nameLabel: String,
    val isFavorite: Boolean = false,
    val isAdotFriend: Boolean = false,
    val createDate: Long,
    override val uiId: Long = dbId, //일반 친구 목록의 경우 dbId를 그대로 사용한다.
    override val uiType: FriendListUiType
): FriendUiModel


