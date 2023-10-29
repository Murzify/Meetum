package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.daysOfWeek
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.feature.calendar.R
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent
import com.murzify.meetum.feature.calendar.components.fake.FakeRepetitiveEventsComponent
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
internal fun RepetitiveEventsPreview() {
    RepetitiveEventsUi(component = FakeRepetitiveEventsComponent())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RepetitiveEventsUi(
    component: RepetitiveEventsComponent
) {
    val everyAmount by component.everyAmount.collectAsState()
    val everyPeriod by component.everyPeriod.collectAsState()
    val daysOfWeek by component.daysOfWeek.collectAsState()
    val showDaysOfWeek by component.showDaysOfWeek.collectAsState()
    val endTimes by component.endTimes.collectAsState()
    val endDate by component.endDate.collectAsState()
    val showDatePicker by component.showDatePicker.collectAsState()
    val endType by component.endType.collectAsState()

    Toolbar(
        titleText = stringResource(id = R.string.repetitive_events),
        fab = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = component::onSaveClicked
            ) {
                Text(
                    text = stringResource(id = R.string.save_record),
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
                text = stringResource(id = R.string.repeat_every),
                fontSize = 16.sp,
                modifier = Modifier.padding(
                    bottom = 8.dp
                )
            )
            Row {
                OutlinedTextField(
                    value = everyAmount.toString(),
                    onValueChange = component::onEveryAmountChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .width(64.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(16.dp)
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
                text = stringResource(id = R.string.end_of_repetition),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Date?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Date().time,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val currentDay = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                   set(Calendar.SECOND, 0)
                  set(Calendar.MILLISECOND, 0)
              }.time
             return utcTimeMillis >= currentDay.time
           }
        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let { Date(it) }

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
    endDate: Date,
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
                                endDate
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
                        TimesField(
                            endTimes = endTimes,
                            onTimesChanged = onTimesChanged
                        )
                        Text(
                            text = text.second,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimesField(
    endTimes: Int,
    onTimesChanged: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val colors = OutlinedTextFieldDefaults.colors()
    val textColor = with(colors) {
        return@with when {
            focused -> focusedTextColor
            else -> unfocusedTextColor
        }
    }
    BasicTextField(
        modifier = Modifier.size(60.dp, height = 35.dp),
        value = endTimes.toString(),
        onValueChange = onTimesChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            color = textColor
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.cursorColor),
        decorationBox = { innerTextField ->

            OutlinedTextFieldDefaults.DecorationBox(
                value = endTimes.toString(),
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = null,
                label = null,
                leadingIcon = null,
                trailingIcon = null,
                prefix = null,
                suffix = null,
                supportingText = null,
                singleLine = true,
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                colors = OutlinedTextFieldDefaults.colors(),
                contentPadding = PaddingValues(1.dp),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodField(
    periodAmount: Int,
    period: Int,
    onPeriodChanged: (period: Int) -> Unit
) {
    val periodsRes = mapOf(
        Calendar.DATE to R.plurals.day,
        Calendar.WEEK_OF_MONTH to R.plurals.week,
        Calendar.MONTH to R.plurals.month,
        Calendar.YEAR to R.plurals.year,
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(150.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = pluralStringResource(periodsRes[period]!!, periodAmount),
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
                    text = { Text(pluralStringResource(value, periodAmount)) },
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
