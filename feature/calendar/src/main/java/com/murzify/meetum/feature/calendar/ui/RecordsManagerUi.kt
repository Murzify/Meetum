package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.feature.calendar.R
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun RecordsManagerUi(
    component: RecordsManagerComponent
) {
    val selectedDate by component.selectedDate.collectAsState()
    val listState = rememberLazyListState()

    val allRecords by component.allRecords.collectAsState()
    val records by component.currentRecords.collectAsState()
    val splitScreen = calculateWindowSizeClass().widthSizeClass != WindowWidthSizeClass.Compact

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (splitScreen) {
                Calendar(
                    weight = 1f,
                    allRecords = allRecords,
                    selectedDate = selectedDate,
                    selectDate = component::onDateClick
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!splitScreen) {
                    item {
                        Calendar(
                            weight = 1f,
                            selectDate = component::onDateClick,
                            allRecords = allRecords,
                            selectedDate = selectedDate,
                        )
                    }
                }
                item {
                    DayTitle(selectedDate = selectedDate)
                }
                items(records) {
                    RecordCard(
                        it,
                        onClick = component::onRecordClick
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
            onClick = {
                component.onAddRecordClick()
            }
        ) {
            Icon(
                painter = painterResource(id = com.murzify.ui.R.drawable.round_add_24),
                contentDescription = stringResource(id = R.string.add_record)
            )
        }
    }

}

@Composable
private fun RowScope.Calendar(
    weight: Float,
    allRecords: List<Record>,
    selectedDate: LocalDate,
    selectDate: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfGrid
    )

    HorizontalCalendar(
        state = state,
        dayContent = {
            Day(
                it,
                isSelected = selectedDate == it.date,
                showBadge = allRecords.any { record ->
                    val recordDates =
                        record.time.map { date ->
                            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                    recordDates.contains(it.date)
                }
            ) { day ->
                if (selectedDate != day.date) {
                    selectDate(day.date)
                }
            }
        },
        monthHeader = {
            Month(month = it)
            DaysOfWeekTitle(
                daysOfWeek = daysOfWeek(firstDayOfWeek = firstDayOfWeek)
            )
        },
        monthBody = { _, container ->
            Box(modifier = Modifier.padding(8.dp)) {
                container()
            }
        },
        modifier = Modifier
            .weight(weight)
    )
}

@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    showBadge: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
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
                .background(
                    if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent
                )
                .clickable(
                    enabled = day.position == DayPosition.MonthDate,
                    onClick = { onClick(day) }
                ),
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
private fun DayTitle(
    selectedDate: LocalDate,
) {
    val f = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
    val date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault())?.toInstant())
    val dateFormatted = f.format(
        date
    )
    Text(
        text = dateFormatted,
        modifier = Modifier.padding(8.dp)
    )
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

@Composable
private fun RecordCard(
    record: Record,
    onClick: (record: Record) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClick(record)
            }) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!record.clientName.isNullOrEmpty()) {
                        Text(
                            text = record.clientName!!,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(text = record.service.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

                Text(text = sdf.format(record.time[0]), modifier = Modifier.padding(start = 2.dp))
            }
        }
    }
}