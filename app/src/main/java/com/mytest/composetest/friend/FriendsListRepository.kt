package com.mytest.composetest.friend

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mytest.composetest.friend.data.FriendPagingSource
import com.mytest.composetest.friend.db.FriendSortOrder
import com.mytest.composetest.friend.db.FriendView
import com.mytest.composetest.friend.db.FriendsDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FriendsListRepository @Inject constructor(private val friendDb: FriendsDatabase) {
    companion object {
        const val PAGE_SIZE = 20 //20개씩 읽는다.
    }

    //paging 사용시 적용
    fun getFriendsPagingDataFlow(): Flow<PagingData<FriendView>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FriendPagingSource(friendDb) }
        ).flow
    }

    fun getFriendsFlow(friendOrder: FriendSortOrder) = friendDb.friendsDao().getAllFriends(friendOrder)
}