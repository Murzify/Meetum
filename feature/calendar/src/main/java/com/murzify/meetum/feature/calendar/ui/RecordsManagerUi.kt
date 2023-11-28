package com.murzify.meetum.feature.calendar.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.murzify.meetum.feature.calendar.components.fake.FakeRecordsManagerComponent
import com.murzify.ui.R.drawable
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
internal fun RecordsManagerPreview() {
    RecordsManagerUi(component = FakeRecordsManagerComponent())
}

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
internal fun RecordsManagerUi(
    component: RecordsManagerComponent
) {
    val model by component.model.collectAsState()
    val splitScreen = if (LocalInspectionMode.current) {
        false
    } else {
        calculateWindowSizeClass().widthSizeClass != WindowWidthSizeClass.Compact
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                onClick = {
                    component.onAddRecordClick()
                }
            ) {
                Icon(
                    painter = painterResource(id = drawable.round_add_24),
                    contentDescription = stringResource(id = R.string.add_record)
                )
            }
        }
    ) { paddingValues ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (splitScreen) {
                Calendar(
                    weight = 1f,
                    allRecords = model.allRecords,
                    selectedDate = model.selectedDate,
                    selectDate = component::onDateClick,
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = paddingValues
            ) {
                if (!splitScreen) {
                    item {
                        Calendar(
                            weight = 1f,
                            selectDate = component::onDateClick,
                            allRecords = model.allRecords,
                            selectedDate = model.selectedDate,
                        )
                    }
                }
                item {
                    DayTitle(selectedDate = model.selectedDate)
                }
                items(
                    model.currentRecords,
                    key = {
                        it.hashCode().toString() + it.time[0]
                    }
                ) { record ->
                    var show by remember {
                        mutableStateOf(true)
                    }
                    val dismissSate = rememberDismissState(
                        confirmValueChange = {
                            if (it == DismissValue.DismissedToStart ||
                                it == DismissValue.DismissedToEnd) {
                                show = false
                                component.onDismissToStart(record)
                                true
                            } else false
                        }
                    )
                    Column(modifier = Modifier.animateItemPlacement()) {
                        SwipeToDismiss(
                            state = dismissSate,
                            background = {
                                DismissBackground(dismissSate = dismissSate)
                            },
                            dismissContent = {
                                RecordCard(
                                    record,
                                    onClick = component::onRecordClick
                                )
                            },
                            directions = mutableSetOf(DismissDirection.StartToEnd).apply {
                                if (!splitScreen) add(DismissDirection.EndToStart)
                            }
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(dismissSate: DismissState) {
    val color by animateColorAsState(
        when (dismissSate.targetValue) {
            DismissValue.Default -> Color.Transparent
            else -> MaterialTheme.colorScheme.errorContainer
        }, label = ""
    )
    val contentAlignment = when (dismissSate.targetValue) {
        DismissValue.Default -> Alignment.Center
        DismissValue.DismissedToEnd -> Alignment.CenterStart
        DismissValue.DismissedToStart -> Alignment.CenterEnd
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize()
            .background(color = color),
        contentAlignment = contentAlignment
    ) {
        Icon(
            painter = painterResource(id = drawable.round_delete_outline_24),
            contentDescription = "",
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun RowScope.Calendar(
    weight: Float,
    allRecords: List<Record>,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
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
    val tz = TimeZone.currentSystemDefault()


    HorizontalCalendar(
        state = state,
        dayContent = {
            Day(
                it,
                isSelected = selectedDate == it.date.toKotlinLocalDate(),
                showBadge = allRecords.any { record ->
                    record.clientName
                    val recordDates =
                        record.time.map { date ->
                            date.toLocalDateTime(tz).date
                        }
                    recordDates.contains(it.date.toKotlinLocalDate())
                }
            ) { day ->
                val kotlinLocalDate = day.date.toKotlinLocalDate()
                if (selectedDate != kotlinLocalDate) {
                    selectDate(kotlinLocalDate)
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
        modifier = modifier
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
    val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val border = remember { now == day.date.toKotlinLocalDate() }
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
private fun DayTitle(
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

@Composable
private fun RecordCard(
    record: Record,
    onClick: (record: Record) -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick(record)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp),
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
        val date = Date.from(record.time[0].toJavaInstant())
        Text(
            text = sdf.format(date),
            modifier = Modifier.padding(start = 2.dp, end = 16.dp)
        )
    }
}