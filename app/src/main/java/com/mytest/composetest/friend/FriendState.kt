package com.mytest.composetest.friend

import com.mytest.composetest.friend.data.FriendUiModel

// 친구 리스트 로팅 결과 표시
sealed interface FriendLoadingStatus
object FriendLoading : FriendLoadingStatus
data class FriendLoadSuccess(val friends: List<FriendUiModel>) : FriendLoadingStatus
data class FriendLoadFailed(val e: Throwable) : FriendLoadingStatus
