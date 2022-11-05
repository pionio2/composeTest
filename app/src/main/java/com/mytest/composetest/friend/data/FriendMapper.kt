package com.mytest.composetest.friend.data

import androidx.paging.PagingData
import androidx.paging.map
import com.mytest.composetest.friend.db.FriendEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun FriendEntity.asDomain() = FriendModel(
    id = this.id ?: -1,
    name = this.name,
    phoneNumber = this.phoneNumber
)

fun Flow<PagingData<FriendEntity>>.toFriendModel(): Flow<PagingData<FriendModel>> = map { pagingData -> pagingData.map { it.asDomain() } }