package com.example.composetest.calendar

class CalendarInfo(val startDayIndex: Int, val lastDayIndex: Int, val weeks: Int, val previousMonthLastDay:Int) {
    fun startDateOfRow(): Int {
        val emptyDate = startDayIndex - 1
        return previousMonthLastDay - emptyDate - 1
    }
}