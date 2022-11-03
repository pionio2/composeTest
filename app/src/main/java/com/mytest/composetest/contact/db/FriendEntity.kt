package com.mytest.composetest.contact.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytest.composetest.contact.db.FriendEntity.Companion.TABLE_FRIENDS

@Entity(tableName = TABLE_FRIENDS)
data class FriendEntity(
    @PrimaryKey
    @ColumnInfo(name = Columns.ID)
    var id: Long? = null,
    @ColumnInfo(name = Columns.NAME)
    var securitiesCompany: String = "",
    @ColumnInfo(name = Columns.PHONE_NUMBER)
    var numberOfShares: String = ""
) {
    companion object {
        const val TABLE_FRIENDS = "friends"
    }

    class Columns {
        companion object {
            const val ID = "_id"
            const val NAME = "name"
            const val PHONE_NUMBER = "phone_number"
        }
    }
}