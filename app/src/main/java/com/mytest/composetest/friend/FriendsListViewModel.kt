package com.mytest.composetest.friend

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mytest.composetest.friend.data.FriendModel
import com.mytest.composetest.friend.data.FriendPagingSource
import com.mytest.composetest.friend.data.toFriendModel
import com.mytest.composetest.friend.db.FriendEntity
import com.mytest.composetest.friend.db.FriendsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val friendRepository: FriendsListRepository,
    private val friendDb: FriendsDatabase,

    ) : ViewModel() {

    lateinit var friendsListFlow : Flow<PagingData<FriendModel>>

    init {
        val pagingSource = FriendPagingSource(friendDb)
        friendsListFlow = friendRepository.getContactResultFlow(pagingSource).toFriendModel().cachedIn(viewModelScope)
    }
}