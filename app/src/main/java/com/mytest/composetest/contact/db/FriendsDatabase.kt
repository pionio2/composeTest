package com.mytest.composetest.contact.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mytest.composetest.util.LogError
import java.util.stream.IntStream.range

@Database(
    entities = [FriendEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FriendsDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao

    companion object {
        const val DB_NAME = "friends.db"
        private const val TAG = "FriendsDatabase"

        val friendsDbCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 초기값 입력.
                insertDefaultData(db)
            }
        }

        private fun insertDefaultData(db: SupportSQLiteDatabase) {
            try {
                db.beginTransaction()
                makeInsertSql(db, "둘리", "010-1111-")
                makeInsertSql(db, "또치", "010-2222-")
                makeInsertSql(db, "도우너", "010-3333-")
                makeInsertSql(db, "고길동", "010-4444-")
                makeInsertSql(db, "희동이", "010-5555-")
                makeInsertSql(db, "마이콜", "010-6666-")
                makeInsertSql(db, "철수", "010-7777-")
                makeInsertSql(db, "영희", "010-8888-")
                makeInsertSql(db, "나애리", "010-9999-")
                makeInsertSql(db, "하늬", "010-0000-")
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                LogError(TAG, e) { "Failed to insert initial data" }
            } finally {
                db.endTransaction()
            }
        }

        private fun makeInsertSql(db: SupportSQLiteDatabase, name: String, phonePrefix: String) {
            (1..30).forEach {
                val phoneNumber = phonePrefix + it.toString().padStart(4, '0')
                val sql = "INSERT INTO ${FriendEntity.TABLE_FRIENDS} (${FriendEntity.Columns.NAME},${FriendEntity.Columns.PHONE_NUMBER}) VALUES (?,?)"
                db.execSQL(sql, arrayOf("$name$it", phoneNumber))
            }
        }
    }
}