package com.mytest.composetest

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.mytest.composetest.calendar.EventInfo
import com.mytest.composetest.calendar.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private val _timeoutText = MutableStateFlow("Timeout!!")
    val timeoutText: StateFlow<String> = _timeoutText

    private val _currentCalendar = MutableLiveData(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar


    private val _datesOfCalendar =  MediatorLiveData<List<EventItem>>().apply {
        addSource(_currentCalendar) { value = calculateCalendar(it) }
    }
    val mEventsOfEvent : LiveData<List<EventItem>> = _datesOfCalendar

    fun onNameChange(newName: String) {
        _name.value = newName
        _error.value = newName == "error"
    }

    suspend fun changeTimeoutText(timeoutText: String) {
        withContext(Dispatchers.Main) {
            _timeoutText.emit(timeoutText)
        }
    }

    fun calculateCalendar(calendar: Calendar = Calendar.getInstance()): List<EventItem> {
        // 전달 마지막 말
        val prevMonthLastDate = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 이번달 마지막 날
        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) //28, 29, 30, 31

        // 이번달 1일 / 말일의 요일을 구한다 1:일, 2:월, 3:화, 4:수, 5:목, 6:금, 7:토
        val startDayIndex = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)
        val lastDayIndex = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, lastDate) }.get(Calendar.DAY_OF_WEEK)

        val todayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val calendarDates = mutableListOf<EventItem>()

        // 이번달 1일 앞에 보여지는 전달 자투리 날짜들
        val dateForPrevMonth = startDayIndex - 1
        if (dateForPrevMonth != 0) {
            val startDayOfFirstRow =  prevMonthLastDate - dateForPrevMonth + 1
            repeat(dateForPrevMonth) {
                calendarDates.add(EventItem(startDayOfFirstRow + it, listOf(), false))
            }
        }

        val sample = listOf(
            EventInfo("테스트 일정1"),
            EventInfo("테스트 일정2", Color.Red),
            EventInfo("테스트 일정3", Color.Green)
        )

        for (date in 1..lastDate) {
            calendarDates.add(EventItem(date, sample, true, date == todayDate))
        }


        // 이번달 말일 뒤에 보여지는 다음달 자투리 날짜들
        val dateForNextMonth = 7 - lastDayIndex
        if (dateForNextMonth != 0) {
            repeat(dateForNextMonth) {
                calendarDates.add(EventItem( it + 1, listOf(), false))
            }
        }

        return calendarDates
    }
}