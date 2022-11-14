package com.mytest.composetest.friend.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mytest.composetest.util.LogError

@Database(
    entities = [FriendEntity::class],
    version = 1,
    exportSchema = false,
    views = [FriendView::class]
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
                makeInsertSql(db, "pineapple", "011-5555-")
                makeInsertSql(db, "startFruite", "011-6666-")
                makeInsertSql(db, "melon", "011-7777-")
                makeInsertSql(db, "watermelon", "011-8888-")
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
                makeInsertSql(db, "apple", "011-1111-")
                makeInsertSql(db, "pear", "011-2222-")
                makeInsertSql(db, "grape", "011-3333-")
                makeInsertSql(db, "banana", "011-4444-")
                makeInsertSql(db, "100", "012-0000-")
                makeInsertSql(db, "200", "012-1111-")
                makeInsertSql(db, "300", "012-2222-")
                makeInsertSql(db, "400", "012-3333-")
                makeInsertSql(db, "500", "012-4444-")
                makeInsertSql(db, "500", "012-5555-")
                makeInsertSql(db, "500", "012-6666-")
                makeInsertSql(db, "#$", "013-0000-")
                makeInsertSql(db, "&$%", "013-1111-")
                makeInsertSql(db, "*!@", "013-2222-")
                makeInsertSql(db, "가가멜", "013-3333-",1, 0, 1)
                makeInsertSql(db, "Schtroumpf", "013-3333-",1, 0, 1)
                makeInsertSql(db, "에이닷친구", "013-4444-",0, 1, 1)
                makeInsertSql(db, "Adot Friend", "013-4444-",0, 1, 1)
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                LogError(TAG, e) { "Failed to insert initial data" }
            } finally {
                db.endTransaction()
            }
        }

        private fun makeInsertSql(db: SupportSQLiteDatabase,
                                  name: String,
                                  phonePrefix: String,
                                  isFavorite: Int = 0,
                                  isAdotUser: Int = 0,
                                  repeat: Int = 100) {
            (1..repeat).forEach {
                val phoneNumber = phonePrefix + it.toString().padStart(4, '0')
                val sql = """ INSERT INTO ${FriendEntity.TABLE_FRIENDS} (
                        ${FriendEntity.Columns.NAME}, ${FriendEntity.Columns.PHONE_NUMBER}, ${FriendEntity.Columns.IS_FAVORITE},
                        ${FriendEntity.Columns.IS_ADOT_USER}, ${FriendEntity.Columns.CREATE_DATE})
                        VALUES (?,?,?,?,?)
                        """
                db.execSQL(sql, arrayOf("$name$it", phoneNumber, isFavorite, isAdotUser, System.currentTimeMillis()))
            }
        }
    }
}