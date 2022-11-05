package com.mytest.composetest.friend.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mytest.composetest.friend.FriendsListRepository
import com.mytest.composetest.friend.db.FriendEntity
import com.mytest.composetest.friend.db.FriendsDatabase
import com.mytest.composetest.util.LogDebug
import com.mytest.composetest.util.LogError

class FriendPagingSource(private val friendsDb: FriendsDatabase) : PagingSource<Int, FriendEntity>() {
    companion object {
        private const val TAG = "FriendPagingSource"
        private const val START_PAGE = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FriendEntity> {
        val page = params.key ?: START_PAGE
        val loadSize = params.loadSize
        LogDebug(TAG) { "load() - page:$page | loadSize:$loadSize" }

        return try {
            val friendsList = friendsDb.friendsDao().getFriends(page, loadSize)
            LoadResult.Page(
                data = friendsList,
                prevKey = if (page == START_PAGE) null else page - 1,
                nextKey = if (friendsList.isEmpty()) null else page + (loadSize / FriendsListRepository.PAGE_SIZE)
            )
        } catch (e: Exception) {
            LogError(TAG) { "load() - failed to load data from db" }
            LoadResult.Error(e)
        }
    }

    // refresh key는 Paging library가 아예 데이터 리스트를 전부 다시 갱신해야 할때
    override fun getRefreshKey(state: PagingState<Int, FriendEntity>): Int? {
        // Anchor의 위치는 가장 최근에 접근한 index임.
        // 이 index와 가장 인접한 페이지의 previous key를 가져오도록 한다. (만약 null이라면 next key의 값을 사용)
        val anchorPosition = state.anchorPosition
        return if (anchorPosition != null) {
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?:state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        } else {
            null
        }
    }
}