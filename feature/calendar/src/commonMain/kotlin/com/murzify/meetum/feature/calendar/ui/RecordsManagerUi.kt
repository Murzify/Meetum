package com.murzify.meetum.feature.calendar.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.add_record
import com.murzify.meetum.core.ui.resources.round_add_24
import com.murzify.meetum.core.ui.resources.round_delete_outline_24
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun RecordsManagerUi(
    component: RecordsManagerComponent
) {
    val model by component.model.collectAsState()
    var scaffoldWidth by remember { mutableStateOf(0.dp) }
    val isCompact = calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact
    val splitScreen = scaffoldWidth > 600.dp && !isCompact

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfGrid
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                onClick = {
                    component.onAddRecordClick()
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.round_add_24),
                    contentDescription = stringResource(Res.string.add_record)
                )
            }
        },
        modifier = Modifier.onGloballyPositioned { coordinates ->
            scaffoldWidth = coordinates.size.width.dp
        }
    ) { paddingValues ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (splitScreen) {
                Calendar(
                    state = calendarState,
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
                            state = calendarState,
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
                        it.hashCode().toString() + it.time.id
                    }
                ) { currentRecord ->
                    var show by remember {
                        mutableStateOf(true)
                    }
                    val dismissSate = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart ||
                                it == SwipeToDismissBoxValue.StartToEnd
                            ) {
                                show = false
                                component.onDismissToStart(currentRecord)
                                true
                            } else false
                        }
                    )
                    Column(modifier = Modifier) {
                        SwipeToDismissBox(
                            state = dismissSate,
                            backgroundContent = {
                                DismissBackground(dismissSate = dismissSate)
                            },
                            content = {
                                RecordCard(
                                    currentRecord.record,
                                    currentRecord.time,
                                    onClick = {
                                        component.onRecordClick(
                                            currentRecord.record,
                                            currentRecord.time
                                        )
                                    }
                                )
                            },
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

@Composable
private fun DismissBackground(dismissSate: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        MaterialTheme.colorScheme.errorContainer, label = ""
    )
    val contentAlignment = when (dismissSate.targetValue) {
        SwipeToDismissBoxValue.Settled -> Alignment.Center
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize()
            .background(color = color),
        contentAlignment = contentAlignment
    ) {
        Icon(
            painter = painterResource(Res.drawable.round_delete_outline_24),
            contentDescription = "",
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun RowScope.Calendar(
    state: CalendarState,
    weight: Float,
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
    recordTime: RecordTime,
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
        val date = Date.from(recordTime.time.toJavaInstant())
        Text(
            text = sdf.format(date),
            modifier = Modifier.padding(start = 2.dp, end = 16.dp)
        )
    }
}