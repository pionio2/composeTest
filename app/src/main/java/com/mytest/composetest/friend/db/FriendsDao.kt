package com.mytest.composetest.friend.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(repos: List<FriendEntity>)

//    @Query(
//        """SELECT * FROM ${FriendEntity.TABLE_FRIENDS}
//              ORDER BY ${FriendEntity.Columns.NAME}
//              LIMIT :loadSize OFFSET :page * :loadSize"""
//    )
//    abstract suspend fun getFriends(page: Int, loadSize: Int): List<FriendEntity>

    @Query("DELETE FROM ${FriendEntity.TABLE_FRIENDS}")
    abstract suspend fun deleteAllFriends()

    /**
     * paging 지원 - page 친구 리스트를 단위를 읽는다. (paging 이용시 사용)
     * view table을 통해서 초성이 포함되고, 주어진 order에 따라 읽어온다.
     * 기본 정렬은 한글 -> 영문 -> 숫자/특수문자순이지만 전달된 param을 통해서 조정 가능하다.
     */
    @Query(
        """
            SELECT *,
                CASE
                    WHEN ${FriendView.Columns.FIRST_CHAR} >='ㄱ' and ${FriendView.Columns.FIRST_CHAR} <='힣' THEN :hangulOrder
                    WHEN ${FriendView.Columns.FIRST_CHAR} >='A' and ${FriendView.Columns.FIRST_CHAR} <='z' THEN :englishOrder
                    ELSE :specialCharOrder
                END AS label_order
            FROM ${FriendView.VIEW_FRIENDS_LIST} 
            ORDER BY label_order, ${FriendEntity.Columns.NAME} 
            LIMIT :loadSize OFFSET :page * :loadSize
        """
    )
    protected abstract suspend fun getFriendsForPaging(
        page: Int,
        loadSize: Int,
        hangulOrder: Int,
        englishOrder: Int,
        specialCharOrder: Int,
    ): List<FriendView>


    // paging 지원 - 친구 리스트를 가져온다.
    suspend fun getFriendsForPaging(
        page: Int,
        loadSize: Int,
        friendOrder: FriendSortOrder = FriendSortOrder()
    ) = getFriendsForPaging(
        page, loadSize, friendOrder.hangulOrder,
        friendOrder.englishOrder,
        friendOrder.specialCharacterOrder
    )

    /**
     * paging 지원 - page 친구 리스트를 단위를 읽는다. (paging 이용시 사용)
     * view table을 통해서 초성이 포함되고, 주어진 order에 따라 읽어온다.
     * 기본 정렬은 한글 -> 영문 -> 숫자/특수문자순이지만 전달된 param을 통해서 조정 가능하다.
     */
    @Query(
        """
            SELECT *,
                CASE
                    WHEN ${FriendView.Columns.FIRST_CHAR} >='ㄱ' and ${FriendView.Columns.FIRST_CHAR} <='힣' THEN :hangulOrder
                    WHEN ${FriendView.Columns.FIRST_CHAR} >='A' and ${FriendView.Columns.FIRST_CHAR} <='z' THEN :englishOrder
                    ELSE :specialCharOrder
                END AS label_order
            FROM ${FriendView.VIEW_FRIENDS_LIST} 
            ORDER BY label_order, ${FriendEntity.Columns.NAME}
        """
    )
    protected abstract fun getAllFriends(
        hangulOrder: Int,
        englishOrder: Int,
        specialCharOrder: Int,
    ): Flow<List<FriendView>>


    // paging 지원 - 친구 리스트를 가져온다.
    fun getAllFriends(friendOrder: FriendSortOrder) = getAllFriends(
        friendOrder.hangulOrder,
        friendOrder.englishOrder,
        friendOrder.specialCharacterOrder
    )
}