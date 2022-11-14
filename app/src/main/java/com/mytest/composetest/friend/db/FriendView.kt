package com.mytest.composetest.friend.db

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.mytest.composetest.friend.FriendsListViewModel

/**
 * 초성 및 정렬 기준을 포함한 데이터를 반환한다.
 *
 * select *,
 *      case
 *          when first_char >='ㄱ' and first_char <='낗' then 'ㄱ'
 *          when first_char >='ㄴ' and first_char <='닣' then 'ㄴ'
 *          when first_char >='ㄷ' and first_char <='띻' then 'ㄷ'
 *          when first_char >='ㄹ' and first_char <='맇' then 'ㄹ'
 *          when first_char >='ㅁ' and first_char <='밓' then 'ㅁ'
 *          when first_char >='ㅂ' and first_char <='삫' then 'ㅂ'
 *          when first_char >='ㅅ' and first_char <='앃' then 'ㅅ'
 *          when first_char >='ㅇ' and first_char <='잏' then 'ㅇ'
 *          when first_char >='ㅈ' and first_char <='찧' then 'ㅈ'
 *          when first_char >='ㅊ' and first_char <='칳' then 'ㅊ'
 *          when first_char >='ㅋ' and first_char <='킿' then 'ㅋ'
 *          when first_char >='ㅌ' and first_char <='팋' then 'ㅌ'
 *          when first_char >='ㅍ' and first_char <='핗' then 'ㅍ'
 *          when first_char >='ㅎ' and first_char <='힣' then 'ㅎ'
 *          when first_char >='A' and first_char <='z' then first_char
 *          else '#'
 *      end as chosung
 * from (
 *      select *, substr(name, 1, 1) as first_char
 *      from friends
 * )
 */
@DatabaseView(
    viewName = FriendView.VIEW_FRIENDS_LIST,
    value = """
    select *, 
        case
            when first_char >='ㄱ' and first_char <='낗' then 'ㄱ'        
            when first_char >='ㄴ' and first_char <='닣' then 'ㄴ'
            when first_char >='ㄷ' and first_char <='띻' then 'ㄷ'        
            when first_char >='ㄹ' and first_char <='맇' then 'ㄹ'
            when first_char >='ㅁ' and first_char <='밓' then 'ㅁ'
            when first_char >='ㅂ' and first_char <='삫' then 'ㅂ'        
            when first_char >='ㅅ' and first_char <='앃' then 'ㅅ'        
            when first_char >='ㅇ' and first_char <='잏' then 'ㅇ'
            when first_char >='ㅈ' and first_char <='찧' then 'ㅈ'        
            when first_char >='ㅊ' and first_char <='칳' then 'ㅊ'
            when first_char >='ㅋ' and first_char <='킿' then 'ㅋ'
            when first_char >='ㅌ' and first_char <='팋' then 'ㅌ'
            when first_char >='ㅍ' and first_char <='핗' then 'ㅍ'
            when first_char >='ㅎ' and first_char <='힣' then 'ㅎ'
            when first_char >='A' and first_char <='z' then upper(first_char)
            else '#'
        end as name_label,
        case
            when first_char >='ㄱ' and first_char <='힣' then ${FriendView.KOREA_LABEL_BUCKET}
            when first_char >='A' and first_char <='z' then ${FriendView.ENGLISH_LABEL_BUCKET}
            else ${FriendView.OTHER_LABEL_BUCKET}
        end as label_bucket
    from (
        select *, substr(name, 1, 1) as first_char
        from friends
    )    
    """
)
data class FriendView(
    @Embedded val friend: FriendEntity,
    @ColumnInfo(name = Columns.FIRST_CHAR)
    val firstChar: String,
    @ColumnInfo(name = Columns.NAME_LABEL)
    val nameLabel: String,
    @ColumnInfo(name = Columns.LABEL_BUCKET)
    val labelBucket: Int,
) {
    companion object {
        const val VIEW_FRIENDS_LIST = "v_friends"
        const val KOREA_LABEL_BUCKET = 1
        const val ENGLISH_LABEL_BUCKET = 2
        const val OTHER_LABEL_BUCKET = 3
    }

    class Columns {
        companion object {
            const val FIRST_CHAR = "first_char"
            const val NAME_LABEL = "name_label"
            const val LABEL_BUCKET = "label_bucket"
        }
    }
}