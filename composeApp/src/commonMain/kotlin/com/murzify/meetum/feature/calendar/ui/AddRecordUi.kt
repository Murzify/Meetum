package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.MR
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.ui.AddServiceCard
import com.murzify.meetum.core.ui.ServiceCard
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.core.ui.local
import com.murzify.meetum.core.ui.moveFocusDown
import com.murzify.meetum.feature.calendar.components.AddRecordComponent
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.DeleteType
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.text.DateFormat
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
internal fun AddRecordUi(
    component: AddRecordComponent
) {
    val model by component.model.collectAsState()
    val tz = TimeZone.currentSystemDefault()
    val localDateTime = model.date.toLocalDateTime(tz)

    val timePickerState = remember {
        TimePickerState(
            initialHour = localDateTime.hour,
            initialMinute = localDateTime.minute,
            is24Hour = true
        )
    }
    component.onTimeChanged(timePickerState.hour, timePickerState.minute)

    val focusManager = LocalFocusManager.current

    Toolbar(
        title = {
            RecordDate(localDateTime = localDateTime)
        },
        onBackClicked = component::onBackClick,
        fab = {
            FloatActionBar(
                canDelete = model.record != null,
                delete = component::onDeleteClicked,
                save = component::onSaveClicked
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TimeInput(
                    modifier = Modifier,
                    state = timePickerState
                )
                IconButton(
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = component::onRepeatClicked
                ) {
                    Icon(
                        painter = painterResource("drawable/round_repeat_24.xml"),
                        contentDescription = stringResource(MR.strings.import_contact)
                    )
                }
            }

            if (model.showRepeatInfo) {
                RepeatText(
                    repeat = model.repeat,
                    modifier = Modifier
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .width(250.dp)
                        .moveFocusDown(focusManager),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    value = model.name,
                    onValueChange = component::onNameChanged,
                    leadingIcon = {
                        Icon(
                            painter = painterResource("drawable/round_person_24.xml"),
                            contentDescription = stringResource(MR.strings.client_name_label)
                        )
                    },
                    label = {
                        Text(text = stringResource(MR.strings.client_name_label))
                    },
                )

                ImportContactButton(
                    onClick = component::onContactsClicked
                )
            }

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(250.dp)
                    .moveFocusDown(focusManager),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                maxLines = Int.MAX_VALUE,
                value = model.description,
                onValueChange = component::onDescriptionChanged,
                leadingIcon = {
                    Icon(
                        painter = painterResource("drawable/round_description_24.xml"),
                        contentDescription = stringResource(MR.strings.description_label)
                    )
                },
                label = {
                    Text(text = stringResource(MR.strings.description_label))
                }
            )

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(250.dp)
                    .moveFocusDown(focusManager),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Phone),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(true) }
                ),
                value = model.phone,
                maxLines = Int.MAX_VALUE,
                onValueChange = component::onPhoneChanged,
                leadingIcon = {
                    Icon(
                        painter = painterResource("drawable/round_phone_24.xml"),
                        contentDescription = stringResource(MR.strings.phone_label)
                    )
                },
                label = {
                    Text(text = stringResource(MR.strings.phone_label))
                }
            )

            Text(
                text = stringResource(MR.strings.choose_service),
                color = if (model.isServiceError) MaterialTheme.colorScheme.error else Color.Unspecified,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

            LazyRow {
                item {
                    Spacer(modifier = Modifier.width(24.dp))
                }
                item {
                    AddServiceCard(modifier = Modifier.width(180.dp)) {
                        component.onAddServiceClick()
                    }
                }
                items(model.services) { service ->
                    ServiceCard(
                        service = service,
                        modifier = Modifier.width(180.dp),
                        border = if (model.service == service) {
                            BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                        } else null,
                        onClick = component::onServiceSelected
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }

        if (model.showSeriesAlert) {
            DeleteAlert(
                onDeleteCanceled = component::onDeleteCancel,
                onDeleteSelected = component::onAlertDeleteTypeSelected
            )
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun FloatActionBar(
    canDelete: Boolean,
    delete: () -> Unit,
    save: () -> Unit,
) {
    Row{
        if (canDelete) {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = delete,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    painter = painterResource("drawable/round_delete_outline_24.xml"),
                    contentDescription = stringResource(MR.strings.delete_record)
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            onClick = save
        ) {
            Text(
                text = stringResource(MR.strings.save_record),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAlert(
    onDeleteCanceled: () -> Unit,
    onDeleteSelected: (DeleteType) -> Unit,

) {
    AlertDialog(onDismissRequest = onDeleteCanceled) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(MR.strings.delete_series_alert),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = onDeleteCanceled,
                    ) {
                        Text(stringResource(MR.strings.cancel))
                    }
                    TextButton(
                        onClick = { onDeleteSelected(DeleteType.Date) },
                    ) {
                        Text(stringResource(MR.strings.this_appointment))
                    }
                    TextButton(
                        onClick = { onDeleteSelected(DeleteType.Series) },
                    ) {
                        Text(stringResource(MR.strings.entire_series))
                    }
                }

            }
        }
    }
}

@Composable
private fun RecordDate(localDateTime: LocalDateTime) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        val date = Date.from(
            localDateTime
                .toInstant(TimeZone.currentSystemDefault())
                .toJavaInstant()

        )
        val dateFormat = DateFormat.getDateInstance(
            DateFormat.DEFAULT, Locale.getDefault()
        )
        val dateFormatted = dateFormat.format(
            date
        )
        Text(
            text = dateFormatted,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun RepeatText(repeat: Repeat, modifier: Modifier) {
    val periodsRes = mapOf(
        DateTimeUnit.DAY to MR.plurals.day,
        DateTimeUnit.WEEK to MR.plurals.week,
        DateTimeUnit.MONTH to MR.plurals.month,
        DateTimeUnit.YEAR to MR.plurals.year,
    )
    var repeatText = "${stringResource(MR.strings.repeat_every)} ${repeat.periodCount} " +
            StringDesc.Plural(periodsRes[repeat.period]!!, repeat.periodCount)
    if (repeat.period == DateTimeUnit.WEEK) {
        repeatText += repeat.daysOfWeek.joinToString(
            separator = ", ",
            prefix = "(",
            postfix = ")"
        ) {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    val ending = if (repeat.repeatTimes != null) {
        stringResource(MR.strings.ending,
            stringResource(MR.strings.after_times) + " ${repeat.repeatTimes} " +
            StringDesc.Plural(MR.plurals.times, repeat.repeatTimes!!).local()
        )
    } else {
        val dateFormat = DateFormat.getDateInstance(
            DateFormat.DEFAULT, Locale.getDefault()
        )
        val dateFormatted = dateFormat.format(
            repeat.repeatToDate
        )
        stringResource(MR.strings.ending, dateFormatted)
    }
    Column(modifier = modifier) {
        Text(text = repeatText)
        Text(text = ending)
    }
}


