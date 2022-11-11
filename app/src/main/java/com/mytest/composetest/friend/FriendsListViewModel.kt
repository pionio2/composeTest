package com.mytest.composetest.friend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytest.composetest.friend.data.toFriendModel
import com.mytest.composetest.friend.db.FriendSortOrder
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

        //친구 목록 order는 (한글/영어/특수문자)는 언어 설정을 따른다.
        val friendsSortOrder = FriendSortOrder(FriendSortOrder.OrderType.HANGUL_ENGLISH_OTHERS)
        //친구 목록을 가져온다.
        friendsListFlow = friendRepository.getFriendsFlow(friendsSortOrder).toFriendModel()
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
