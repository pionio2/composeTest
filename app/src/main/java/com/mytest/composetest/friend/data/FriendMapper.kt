package com.mytest.composetest.friend.data

import com.mytest.composetest.friend.db.FriendView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun FriendView.asDomain() = FriendUiModel(
    dbId = this.friend.id ?: -1,
    name = this.friend.name,
    phoneNumber = this.friend.phoneNumber,
    nameLabel = this.nameLabel,
    createDate = this.friend.createDate
)

//fun Flow<PagingData<FriendView>>.toFriendModel(): Flow<PagingData<FriendModel>> = map { pagingData -> pagingData.map { it.asDomain() } }

fun Flow<List<FriendView>>.toFriendModel(): Flow<List<FriendUiModel>> = map { friendView -> friendView.map{it.asDomain()} }