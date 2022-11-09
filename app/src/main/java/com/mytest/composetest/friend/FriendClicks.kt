package com.mytest.composetest.friend

sealed interface FriendClicks
data class ClickMovePage(val page: Int): FriendClicks
