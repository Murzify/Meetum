package com.murzify.meetum.feature.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.murzify.meetum.core.domain.model.Record
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Composable
internal fun MeetumCalendarRoute(
    navigateToAddRecord: (editing: Boolean, date: Date) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    MeetumCalendar(
        records = records,
        getRecords = {
            viewModel.getRecords(it)
        },
        navigateToAddRecord
    )
}

@Composable
internal fun MeetumCalendar(
    records: List<Record>,
    getRecords: (date: Date) -> Unit,
    navigateToAddRecord: (editing: Boolean, date: Date) -> Unit
) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(LocalDate.now()) }


    RecordsList(
        records = records,
        addRecord = {
            selectedDate?.let {
                val date = Date.from(selectedDate!!.atStartOfDay(ZoneId.systemDefault())?.toInstant())
                navigateToAddRecord(false, date)
            }
        }
    ) {
        HorizontalCalendar(
            state = state,
            dayContent = {
                Day(it, isSelected = selectedDate == it.date) { day ->
                    if (selectedDate == day.date) {
                        selectedDate = null
                    } else {
                        selectedDate = day.date
                        val date = Date.from(selectedDate!!.atStartOfDay(ZoneId.systemDefault())?.toInstant())
                        getRecords(date)
                    }
                }
            },
            monthHeader = {
                Month(month = it)
                DaysOfWeekTitle(
                    daysOfWeek = daysOfWeek(firstDayOfWeek = firstDayOfWeek)
                )
            },
            modifier = Modifier.padding(top = 8.dp)
        )
        selectedDate?.let {
            val f = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            val date = Date.from(selectedDate!!.atStartOfDay(ZoneId.systemDefault())?.toInstant())
            val dateFormatted = f.format(
                date
            )
            getRecords(date)
            Text(
                text = dateFormatted,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}

@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    val border = remember { LocalDate.now() == day.date }
    val bgColor = if (day.position == DayPosition.MonthDate) {
        CardDefaults.cardColors()
    } else {
        CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    }
    val textColor = if (day.position == DayPosition.MonthDate) {
        Color.Unspecified
    } else {
        Color.Gray
    }
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp),
        border = if (border) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = bgColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                .clickable(
                    enabled = day.position == DayPosition.MonthDate,
                    onClick = { onClick(day) }
                ),
            contentAlignment = Alignment.Center)
        {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else textColor
            )
        }

    }

}

@Composable
private fun Month(month: CalendarMonth) {
    val monthText = month.yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = monthText.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = month.yearMonth.year.toString(), fontSize = 24.sp)
    }
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}