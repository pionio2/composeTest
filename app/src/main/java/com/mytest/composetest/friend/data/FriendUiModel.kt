package com.mytest.composetest.friend.data

import androidx.compose.runtime.Stable

@Stable
data class FriendUiModel(
    val dbId: Long,
    val name: String,
    val phoneNumber: String,
    val nameLabel: String,
    val createDate: Long,
    val isFavorite: Boolean = false
)
