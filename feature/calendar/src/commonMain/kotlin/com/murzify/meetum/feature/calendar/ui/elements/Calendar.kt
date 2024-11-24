package com.murzify.meetum.feature.calendar.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.murzify.meetum.core.domain.model.Record
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.DateFormat
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@Composable
fun Calendar(
    state: CalendarState,
    allRecords: List<Record>,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    selectDate: (LocalDate) -> Unit
) {
    val tz = TimeZone.currentSystemDefault()


    HorizontalCalendar(
        state = state,
        dayContent = {
            Day(
                it,
                isSelected = selectedDate == it.date,
                showBadge = allRecords.any { record ->
                    record.clientName
                    val recordDates =
                        record.dates.map { date ->
                            date.time.toLocalDateTime(tz).date
                        }
                    recordDates.contains(it.date)
                }
            ) { day ->
                val kotlinLocalDate = day.date
                if (selectedDate != kotlinLocalDate) {
                    selectDate(kotlinLocalDate)
                }
            }
        },
        monthHeader = {
            Month(month = it)
            DaysOfWeekTitle(
                daysOfWeek = daysOfWeek(firstDayOfWeek = state.firstDayOfWeek)
            )
        },
        monthBody = { _, container ->
            Box(modifier = Modifier.padding(8.dp)) {
                container()
            }
        },
        modifier = modifier
    )
}

@Composable
fun DayTitle(
    selectedDate: LocalDate,
) {
    val f = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    val date = Date.from(
        selectedDate.toJavaLocalDate().atStartOfDay(ZoneId.systemDefault())?.toInstant()
    )
    val dateFormatted = f.format(
        date
    )
    Text(
        text = dateFormatted,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    showBadge: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val border = remember { now == day.date }
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

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        label = ""
    ) {
        if (isPressed) {
            isPressed = false
        }
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        border = if (border) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = bgColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent
                )
                .clickable(
                    enabled = day.position == DayPosition.MonthDate,
                    onClick = {
                        isPressed = true
                        onClick(day)
                    }
                )
            ,
            contentAlignment = Alignment.Center)
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (showBadge) {
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(end = 6.dp, bottom = 6.dp)
                            .size(6.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
                    ) {}
                }
            }
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}