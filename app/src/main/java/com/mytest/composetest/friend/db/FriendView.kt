package com.mytest.composetest.friend.db

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

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
            when first_char >='A' and first_char <='z' then lower(first_char)
            else '#'
        end as name_label,
        case
            when first_char >='ㄱ' and first_char <='힣' then 1
            when first_char >='A' and first_char <='z' then 2
            else 3
        end as label_bucket
    from (
        select *, substr(name, 1, 1) as first_char
        from friends
    )
    order by label_bucket, name
    """
)
data class FriendView(
    @Embedded val friend: FriendEntity,
    @ColumnInfo(name = Columns.FIRST_CHAR)
    val firstChar: String,
    @ColumnInfo(name = Columns.NAME_LABEL)
    val nameLabel: String,
) {
    companion object {
        const val VIEW_FRIENDS_LIST = "v_friends"
    }

    class Columns {
        companion object {
            const val FIRST_CHAR = "first_char"
            const val NAME_LABEL = "name_label"
        }
    }
}