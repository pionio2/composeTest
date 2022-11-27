package com.mytest.composetest.friend

import androidx.compose.runtime.Immutable
import com.mytest.composetest.friend.data.FriendUiModel
import kotlinx.collections.immutable.ImmutableList

// 친구 리스트 로팅 결과 표시
sealed interface FriendLoadingStatus
object FriendLoading : FriendLoadingStatus
data class FriendLoadSuccess(val friends: ImmutableList<FriendUiModel>) : FriendLoadingStatus
data class FriendLoadFailed(val e: Throwable) : FriendLoadingStatus
