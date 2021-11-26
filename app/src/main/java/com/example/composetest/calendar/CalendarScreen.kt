package com.example.composetest.calendar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composetest.MainViewModel
import java.util.*


val border = BorderStroke(color = Color.Gray, width = Dp.Hairline)

@Composable
fun IpoCalendar(mainViewModel: MainViewModel = viewModel()) {
    val eventsOfCalendar by mainViewModel.mEventsOfEvent.observeAsState()
    CalendarBody(eventsOfEvent = eventsOfCalendar ?: mainViewModel.calculateCalendar(Calendar.getInstance()), showWeekend = false)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarBody(modifier: Modifier = Modifier, eventsOfEvent: List<EventItem>, showWeekend: Boolean = true) {
    Column(modifier = modifier.fillMaxWidth()) {
        WeekDayHeader(showWeekend = showWeekend)
        val chunkedList = if (showWeekend) {
            eventsOfEvent.chunked(7)
        } else {
            eventsOfEvent.chunked(7).map{it.subList(1,6)}.filter { it.any {eventItem -> eventItem.isThisMonthDate}}
        }
        chunkedList.forEachIndexed { index, item ->
            EventsRow(events = item)
            if (index != chunkedList.lastIndex) {
                Divider(color = Color.LightGray, modifier = Modifier.height(1.dp))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EventsRow(modifier: Modifier = Modifier, events: List<EventItem>) {
    Row(modifier = modifier) {
        events.forEach {
            DayBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp),
                date = it.date,
                dateColor = if (it.isThisMonthDate) Color.Black else Color.LightGray,
                event = it.events
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeekDayHeader(modifier: Modifier = Modifier, showWeekend: Boolean) {
    Column {
        Row(modifier = modifier.fillMaxWidth()) {
            val date = if (showWeekend) {
                listOf("일", "월", "화", "수", "목", "금", "토")
            } else {
                listOf("월", "화", "수", "목", "금")
            }
            date.forEach {
                Text(
                    it,
                    modifier
                        .weight(1f)
                        .padding(start = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }
        Divider(color = Color.LightGray, modifier = Modifier.height(1.dp))
    }
}


@Composable
fun DayBox(modifier: Modifier = Modifier, date: Int, dateColor: Color = Color.Black, event: List<EventInfo>) {
    Column(modifier = modifier) {
        // 날짜
        Text(
            date.toString(),
            Modifier.padding(start = 2.dp),
            maxLines = 1,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = dateColor
        )
        // Event
        event.forEachIndexed { index, item ->
            Surface(color = item.color) {
                Text(item.subject, fontSize = 10.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (index != event.lastIndex) {
                Divider(color = Color.Transparent, modifier = Modifier.height(2.dp))
            }
        }
    }
}
