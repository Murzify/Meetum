package com.murzify.meetum.feature.calendar.ui

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.ui.AddServiceCard
import com.murzify.meetum.core.ui.ServiceCard
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.feature.calendar.R
import com.murzify.meetum.feature.calendar.components.AddRecordComponent
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.DeleteType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

            ConstraintLayout(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                val (
                    textField,
                    button,
                    timeInput,
                    repeatButton,
                    repeatText
                ) = createRefs()
                TimeInput(
                    modifier = Modifier.constrainAs(timeInput) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    state = timePickerState
                )

                IconButton(
                    modifier = Modifier.constrainAs(repeatButton) {
                        top.linkTo(timeInput.top, 12.dp)
                        start.linkTo(timeInput.end)
                    },
                    onClick = component::onRepeatClicked
                ) {
                    Icon(painter = painterResource(
                        id = R.drawable.round_repeat_24
                    ),
                        contentDescription = stringResource(id = R.string.import_contact)
                    )
                }

                if (model.showRepeatInfo) {
                    RepeatText(
                        repeat = model.repeat,
                        modifier = Modifier.constrainAs(repeatText) {
                            top.linkTo(timeInput.bottom)
                            start.linkTo(parent.start)
                        }
                    )
                }

                TextField(
                    modifier = Modifier
                        .constrainAs(textField) {
                            if (model.showRepeatInfo) {
                                top.linkTo(repeatText.bottom, 8.dp)
                            } else {
                                top.linkTo(timeInput.bottom)
                            }
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(250.dp),
                    value = model.name,
                    onValueChange = component::onNameChanged,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.round_person_24),
                            contentDescription = stringResource(id = R.string.client_name_label)
                        )
                    },
                    label = {
                        Text(text = stringResource(id = R.string.client_name_label))
                    },
                )

                ImportContactButton(
                    modifier = Modifier.constrainAs(button) {
                        linkTo(textField.end,button.start, bias = 0f)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(textField.top)
                    },
                    onClick = component::onContactsClicked
                )
            }

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(250.dp),
                maxLines = Int.MAX_VALUE,
                value = model.description,
                onValueChange = component::onDescriptionChanged,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.round_description_24),
                        contentDescription = stringResource(id = R.string.description_label)
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.description_label))
                }
            )

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(250.dp),
                value = model.phone,
                maxLines = Int.MAX_VALUE,
                onValueChange = component::onPhoneChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.round_phone_24),
                        contentDescription = stringResource(id = R.string.phone_label)
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.phone_label))
                }
            )

            Text(
                text = stringResource(id = R.string.choose_service),
                color = if (model.isServiceError) MaterialTheme.colorScheme.error else Color.Unspecified,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

            LazyRow() {
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

@Composable
private fun FloatActionBar(
    canDelete: Boolean,
    delete: () -> Unit,
    save: () -> Unit,
) {
    Row(){
        if (canDelete) {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = delete,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    painter = painterResource(id = com.murzify.ui.R.drawable.round_delete_outline_24),
                    contentDescription = stringResource(id = R.string.delete_record)
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            onClick = save
        ) {
            Text(
                text = stringResource(id = R.string.save_record),
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
                    text = stringResource(id = R.string.delete_series_alert),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = onDeleteCanceled,
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = { onDeleteSelected(DeleteType.Date) },
                    ) {
                        Text(stringResource(id = R.string.this_appointment))
                    }
                    TextButton(
                        onClick = { onDeleteSelected(DeleteType.Series) },
                    ) {
                        Text(stringResource(id = R.string.entire_series))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportContactButton(modifier: Modifier = Modifier, onClick: (uri: Uri, contentResolver: ContentResolver) -> Unit) {
    var importContact by remember { mutableStateOf(false) }
    val contentResolver = LocalContext.current.findActivity().contentResolver
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) {
        it?.let { uri ->
            onClick(uri, contentResolver)
        }
    }

    if (importContact) {
        RequestContactsPermission() {
            LaunchedEffect(key1 = launcher) {
                launcher.launch(null)
                importContact = false
            }
        }
    }

    IconButton(
        modifier = modifier,
        onClick = {
            importContact = true
        },
    ) {
        Icon(painter = painterResource(
            id = R.drawable.round_import_contacts_24
        ),
            contentDescription = stringResource(id = R.string.import_contact)
        )
    }

}

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

private fun hasContactsPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
}

@Composable
private fun RequestContactsPermission(onGranted: @Composable () -> Unit) {
    var isGranted by remember { mutableStateOf(false) }
    if (isGranted) {
        onGranted()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            isGranted = true
        }
    }
    val activity = LocalContext.current.findActivity()
    if (!hasContactsPermission(activity)) {
        SideEffect {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }
}

@Composable
private fun RepeatText(repeat: Repeat, modifier: Modifier) {
    val periodsRes = mapOf(
        DateTimeUnit.DAY to R.plurals.day,
        DateTimeUnit.WEEK to R.plurals.week,
        DateTimeUnit.MONTH to R.plurals.month,
        DateTimeUnit.YEAR to R.plurals.year,
    )
    var repeatText = "${stringResource(R.string.repeat_every)} ${repeat.periodCount} " +
            pluralStringResource(periodsRes[repeat.period]!!, repeat.periodCount)
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
        stringResource(id = R.string.ending,
            stringResource(id = R.string.after) + " ${repeat.repeatTimes} " +
            pluralStringResource(id = R.plurals.times, repeat.repeatTimes!!)
        )
    } else {
        val dateFormat = DateFormat.getDateInstance(
            DateFormat.DEFAULT, Locale.getDefault()
        )
        val dateFormatted = dateFormat.format(
            repeat.repeatToDate
        )
        stringResource(id = R.string.ending, dateFormatted)
    }
    Column(modifier = modifier) {
        Text(text = repeatText)
        Text(text = ending)
    }
}


