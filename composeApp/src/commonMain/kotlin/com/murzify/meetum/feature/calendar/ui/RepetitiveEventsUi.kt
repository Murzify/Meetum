package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.core.ui.local
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

@Composable
internal fun RepetitiveEventsUi(
    component: RepetitiveEventsComponent
) {
    val model by component.model.collectAsState()

    Toolbar(
        title = {
            Text(text = stringResource(MR.strings.repetitive_events))
        },
        fab = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = component::onSaveClicked
            ) {
                Text(
                    text = stringResource(MR.strings.save_record),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        },
        onBackClicked = component::onBackClicked
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(horizontal = 16.dp)
            .scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            Text(
                text = stringResource(MR.strings.repeat_every),
                fontSize = 16.sp,
                modifier = Modifier.padding(
                    bottom = 8.dp
                )
            )
            model.apply {
                Row {
                    TextField(
                        value = everyAmount.toString(),
                        onValueChange = component::onEveryAmountChanged,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 8.dp, bottom = 8.dp),
                    )
                    PeriodField(
                        periodAmount = everyAmount,
                        period = everyPeriod,
                        onPeriodChanged = component::onPeriodChanged
                    )
                }

                if (showDaysOfWeek) {
                    DaysOfWeekSelection(
                        daysOfWeek,
                        component::onDayOfWeekClick
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = stringResource(MR.strings.end_of_repetition),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )

                EndRadio(
                    endType = endType,
                    endTimes = endTimes,
                    endDate = endDate,
                    onEndTypeChanged = component::onEndTypeChanged,
                    onDateClick = component::onPickDateClicked,
                    onTimesChanged = component::onEndTimesChanged
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDateSelected = component::onDatePickerOk,
                        onDismiss = component::onDatePickerCancel
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDateTime?) -> Unit,
    onDismiss: () -> Unit
) {
    val tz = TimeZone.currentSystemDefault()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
//        selectableDates = object : SelectableDates {
//            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//                val localDate = Clock.System.todayIn(tz)
//                val localTime = LocalTime(0, 0, 0)
//             return utcTimeMillis >= LocalDateTime(localDate, localTime)
//                 .toInstant(tz)
//                 .toEpochMilliseconds()
//           }
//        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(tz)
    }

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
private fun EndRadio(
    endType: RepetitiveEventsComponent.EndType,
    endTimes: Int,
    endDate: Instant,
    onEndTypeChanged: (RepetitiveEventsComponent.EndType) -> Unit,
    onDateClick: () -> Unit,
    onTimesChanged: (String) -> Unit
) {
    val radioOptions = listOf(
        RepetitiveEventsComponent.EndType.Times,
        RepetitiveEventsComponent.EndType.Date
    )

    Column(modifier = Modifier.selectableGroup()) {
        radioOptions.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .selectable(
                        selected = type == endType,
                        onClick = { onEndTypeChanged(type) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = type == endType, onClick = null )
                when (type) {
                    RepetitiveEventsComponent.EndType.Date -> {
                        Card(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDateClick
                                ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            val dateFormat = DateFormat.getDateInstance(
                                DateFormat.DEFAULT, Locale.getDefault()
                            )
                            val dateFormatted = dateFormat.format(
                                Date.from(endDate.toJavaInstant())
                            )
                            Text(
                                text = dateFormatted,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    RepetitiveEventsComponent.EndType.Times -> {
                        val timesType = type as
                                RepetitiveEventsComponent.EndType.Times
                        val text = timesType.getText(times = endTimes)
                        Text(
                            text = text.first,
                            modifier = Modifier.padding(6.dp)
                        )
                        TextField(
                            value = endTimes.toString(),
                            onValueChange = onTimesChanged,
                            modifier = Modifier.size(60.dp, 35.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            ),
                            contentPadding = PaddingValues(1.dp)
                        )
                        Text(
                            text = text.second.local(),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DaysOfWeekSelection(
    daysOfWeek: List<DayOfWeek>,
    onDayClick: (DayOfWeek) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
            .padding(vertical = 16.dp),
    ) {
        daysOfWeek().forEach { dayOfWeek ->
            Card(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .width(40.dp)
                    .aspectRatio(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (daysOfWeek.contains(dayOfWeek))
                                MaterialTheme.colorScheme.tertiary
                            else Color.Transparent
                        )
                        .clickable(
                            onClick = { onDayClick(dayOfWeek) }
                        ),
                    contentAlignment = Alignment.Center)
                {
                    Text(
                        text = dayOfWeek.getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        ),
                        color = if (daysOfWeek.contains(dayOfWeek))
                            MaterialTheme.colorScheme.onPrimary
                        else Color.Unspecified
                    )
                }

            }
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

fun daysOfWeek(firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek): List<DayOfWeek> {
    val pivot = 7 - firstDayOfWeek.ordinal
    val daysOfWeek = DayOfWeek.entries.toTypedArray()
    // Order `daysOfWeek` array so that firstDayOfWeek is at the start position.
    return (daysOfWeek.takeLast(pivot) + daysOfWeek.dropLast(pivot))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodField(
    periodAmount: Int,
    period: DateTimeUnit,
    onPeriodChanged: (period: DateTimeUnit) -> Unit
) {
    val periodsRes = mapOf(
        DateTimeUnit.DAY to MR.plurals.day,
        DateTimeUnit.WEEK to MR.plurals.week,
        DateTimeUnit.MONTH to MR.plurals.month,
        DateTimeUnit.YEAR to MR.plurals.year,
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.size(150.dp, 64.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
            value = StringDesc.Plural(periodsRes[period]!!, periodAmount).local(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { TrailingIcon(expanded = expanded) },
            maxLines = 1,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            ),
            shape = RoundedCornerShape(16.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            periodsRes.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Text(StringDesc.Plural(value, periodAmount).local()) },
                    onClick = {
                        expanded = false
                        onPeriodChanged(key)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }

}
