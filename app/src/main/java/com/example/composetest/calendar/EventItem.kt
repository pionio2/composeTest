package com.example.composetest.calendar

class EventItem(val date: Int, // 날짜
                val events: List<EventInfo>, // 포함된 event들
                val isThisMonthDate: Boolean = true, // 해당월에 속한 날짜인지
                val isToday: Boolean = false) //오늘인지 여부