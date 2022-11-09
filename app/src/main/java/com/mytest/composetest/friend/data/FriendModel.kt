package com.mytest.composetest.friend.data

import androidx.compose.runtime.Stable

@Stable
data class FriendModel(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val nameLabel: String,
    val createDate: Long,
)
