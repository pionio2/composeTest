package com.mytest.composetest.contact.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<FriendEntity>)

    @Query("SELECT * FROM ${FriendEntity.TABLE_FRIENDS} ORDER BY ${FriendEntity.Columns.NAME}")
    suspend fun getFriends(): List<FriendEntity>

    @Query("DELETE FROM ${FriendEntity.TABLE_FRIENDS}")
    suspend fun deleteAllFriends()
}