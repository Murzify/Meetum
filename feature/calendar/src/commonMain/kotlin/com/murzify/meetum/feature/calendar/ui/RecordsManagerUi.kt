package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import com.murzify.meetum.core.ui.resources.*
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import com.murzify.meetum.feature.calendar.ui.elements.BookingSwipe
import com.murzify.meetum.feature.calendar.ui.elements.Calendar
import com.murzify.meetum.feature.calendar.ui.elements.DayTitle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RecordsManagerUi(
    component: RecordsManagerComponent
) {
    val model by component.model.collectAsState()
    var scaffoldWidth by remember { mutableStateOf(0.dp) }
    val windowsSizeClass = calculateWindowSizeClass()
    val showCalendarFlipButtons = windowsSizeClass.widthSizeClass != WindowWidthSizeClass.Compact &&
            windowsSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
    val isCompact = windowsSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val splitScreen = scaffoldWidth > 600.dp && !isCompact

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
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
                HorizontalCalendar(
                    showCalendarFlipButtons,
                    currentMonth,
                    calendarState,
                    model,
                    component,
                    paddingValues
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .defaultMinSize(minWidth = 500.dp)
                    .weight(
                        if (showCalendarFlipButtons) 3f else 1f
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = paddingValues
            ) {
                if (!splitScreen) {
                    item {
                        Calendar(
                            state = calendarState,
                            selectDate = component::onDateClick,
                            allRecords = model.allRecords,
                            selectedDate = model.selectedDate,
                            modifier = Modifier.weight(1f)
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
                    BookingSwipe(
                        currentRecord,
                        onClick = {
                            component.onRecordClick(currentRecord.record, currentRecord.time)
                        },
                        onSwiped = {
                            component.onDismissToStart(currentRecord)
                        }
                    )
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
private fun RowScope.HorizontalCalendar(
    showCalendarFlipButtons: Boolean,
    currentMonth: YearMonth,
    calendarState: CalendarState,
    model: RecordsManagerComponent.Model,
    component: RecordsManagerComponent,
    paddingValues: PaddingValues
) {
    var currentMonth1 = currentMonth
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight().weight(
            if (showCalendarFlipButtons) 7f else 1f
        )
    ) {
        if (showCalendarFlipButtons) {
            IconButton(
                onClick = {
                    currentMonth1 = currentMonth1.minusMonths(1)
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_back_ios_24px),
                    "previous"
                )
            }
        }

        Calendar(
            state = calendarState,
            allRecords = model.allRecords,
            selectedDate = model.selectedDate,
            selectDate = component::onDateClick,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .weight(1f, fill = false)
                .heightIn(max = 500.dp)
                .widthIn(max = 500.dp)
        )
        if (showCalendarFlipButtons) {
            IconButton(
                onClick = {
                    currentMonth1 = currentMonth1.plusMonths(1)
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_forward_ios_24px),
                    "next"
                )
            }
        }

    }
}

