package com.mytest.composetest.friend.data

import com.mytest.composetest.friend.db.FriendEntity
import com.mytest.composetest.friend.db.FriendView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.invoke.MethodHandles.throwException

fun FriendView.asDomain(type: FriendListUiType) = FriendUiItem(
    dbId = this.friend.id ?: -1,
    name = this.friend.name,
    phoneNumber = this.friend.phoneNumber,
    nameLabel = this.nameLabel,
    isFavorite = this.friend.isFavorite == FriendEntity.Columns.YES,
    isAdotFriend = this.friend.isAdotUser == FriendEntity.Columns.YES,
    createDate = this.friend.createDate,
    uiId = when(type) { // type에 따른 offset에 dbId를 더하고 음수로 바꿔서 사용한다. (unique함을 보장하기 위해)
      FriendListUiType.FriendItem -> this.friend.id ?: -1L
      is FriendListUiType.AdotItem -> type.uiIdOffset + (this.friend.id ?: -1L) * -1
      is FriendListUiType.FavoriteItem -> type.uiIdOffset + (this.friend.id ?: -1L) * -1
      is FriendListUiType.Header -> -1L // FriendUiItem은 HeaderType을 가질 수 없도록 해야한다.
    },
    uiType = type
)

//fun Flow<PagingData<FriendView>>.toFriendModel(): Flow<PagingData<FriendModel>> = map { pagingData -> pagingData.map { it.asDomain() } }

fun Flow<List<FriendView>>.toFriendModel(type: FriendListUiType): Flow<List<FriendUiItem>> =
    map { friendView -> friendView.map { it.asDomain(type) } }