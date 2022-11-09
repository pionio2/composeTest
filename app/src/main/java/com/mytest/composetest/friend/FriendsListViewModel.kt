package com.mytest.composetest.friend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.mytest.composetest.friend.data.FriendModel
import com.mytest.composetest.friend.data.FriendPagingSource
import com.mytest.composetest.friend.data.toFriendModel
import com.mytest.composetest.friend.db.FriendEntity
import com.mytest.composetest.friend.db.FriendsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val friendRepository: FriendsListRepository,
    private val friendDb: FriendsDatabase
) : ViewModel() {

    companion object {
        private const val FRIENDS_FLOW_TIMEOUT = 5000L
    }

    val friendsListFlow: StateFlow<FriendLoadingStatus>
    //paging 사용시 주석 제거
//    val friendsListFlow: Flow<PagingData<FriendModel>>

    init {
//        friendsListFlow = friendRepository.getFriendsPagingDataFlow()
//            .toFriendModel()
//            .cachedIn(viewModelScope)

        //친구 목록을 가져온다.
        friendsListFlow = friendRepository.getFriendsFlow().toFriendModel()
            .map {
                FriendLoadSuccess(it) as FriendLoadingStatus
            }.catch { cause -> emit(FriendLoadFailed(cause)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = FRIENDS_FLOW_TIMEOUT),
                initialValue = FriendLoading
            )
    }
}
