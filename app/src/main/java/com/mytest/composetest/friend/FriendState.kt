package com.mytest.composetest.friend

import com.mytest.composetest.friend.data.FriendModel
import com.mytest.composetest.friend.db.FriendView

// 친구 리스트 로팅 결과 표시
sealed interface FriendLoadingStatus
object FriendLoading : FriendLoadingStatus
data class FriendLoadSuccess(val friends: List<FriendModel>) : FriendLoadingStatus
data class FriendLoadFailed(val e: Throwable) : FriendLoadingStatus
