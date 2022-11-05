package com.mytest.composetest.friend

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.mytest.composetest.friend.db.FriendEntity
import kotlinx.coroutines.flow.Flow

class FriendsListRepository {
    companion object {
        const val PAGE_SIZE = 20 //20개씩 읽는다.
    }

    fun getContactResultFlow(pageSource: PagingSource<Int, FriendEntity>): Flow<PagingData<FriendEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { pageSource }
        ).flow
    }
}