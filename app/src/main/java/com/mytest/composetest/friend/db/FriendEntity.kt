package com.mytest.composetest.friend.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mytest.composetest.friend.db.FriendEntity.Companion.TABLE_FRIENDS

@Entity(
    tableName = TABLE_FRIENDS,
    indices = [
        Index(value = [FriendEntity.Columns.NAME], unique = false)
    ]
)
data class FriendEntity(
    @PrimaryKey
    @ColumnInfo(name = Columns.ID)
    var id: Long? = null,
    @ColumnInfo(name = Columns.NAME)
    var name: String = "",
    @ColumnInfo(name = Columns.PHONE_NUMBER)
    var phoneNumber: String = "",
    @ColumnInfo(name = Columns.CREATE_DATE)
    var createDate: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE_FRIENDS = "friends"
    }

    class Columns {
        companion object {
            const val ID = "_id"
            const val NAME = "name"
            const val PHONE_NUMBER = "phone_number"
            const val CREATE_DATE = "cr_date"
        }
    }
}