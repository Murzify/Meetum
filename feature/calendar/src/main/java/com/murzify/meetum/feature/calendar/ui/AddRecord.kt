package com.murzify.meetum.feature.calendar.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.ui.AddServiceCard
import com.murzify.meetum.core.ui.ServiceCard
import com.murzify.meetum.feature.calendar.CalendarViewModel
import com.murzify.meetum.feature.calendar.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


@Composable
internal fun AddRecordRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    isEditing: Boolean,
    date: Date,
    navigateToBack: () -> Unit,
    navigateToAddService: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    val selectedRecord by viewModel.selectedRecord.collectAsState()
    AddRecordScreen(
        services,
        navigateToAddService,
        navigateToBack,
        viewModel::deleteRecord,
        viewModel::addRecord,
        viewModel::editRecord,
        isEditing,
        selectedRecord,
        date
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddRecordScreen(
    services: List<Service>,
    navigateToAddService: () -> Unit,
    navigateToBack: () -> Unit,
    delete: () -> Unit,
    save: (record: Record) -> Unit,
    edit: (record: Record) -> Unit,
    isEditing: Boolean,
    record: Record?,
    date: Date
) {
    val defCalendar = Calendar.getInstance().apply {
        time = Date()
        if (isEditing) {
            record?.let {
                time = it.time[0]
            }
        }
    }

    val timePickerState = remember {
        TimePickerState(
            initialHour = defCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = defCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )
    }
    var selectedService: Service? by remember {
        mutableStateOf(
            if (isEditing) {
                record?.service
            } else null
        )
    }
    var clientName by rememberSaveable {
        mutableStateOf(
            if (isEditing) {
                record?.clientName ?: ""
            } else ""
        )
    }
    var description by rememberSaveable {
        mutableStateOf(
            if (isEditing) {
                record?.description ?: ""
            } else ""
        )
    }
    var phoneNumber by rememberSaveable {
        mutableStateOf(
            if (isEditing) {
                record?.phone ?: ""
            } else ""
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    RecordDate(date = record?.time?.get(0) ?: date)
                },
                navigationIcon = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = { navigateToBack() }
                    ) {
                        Icon(
                            painter = painterResource(id = com.murzify.ui.R.drawable.round_arrow_back_24),
                            contentDescription = stringResource(id = com.murzify.ui.R.string.back_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
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
                    onClick = {

                    }
                ) {
                    Icon(painter = painterResource(
                        id = R.drawable.round_repeat_24
                    ),
                        contentDescription = stringResource(id = R.string.import_contact)
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.constrainAs(textField) {
                        top.linkTo(timeInput.bottom)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                    value = clientName,
                    onValueChange = {
                        clientName = it
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.round_person_24),
                            contentDescription = stringResource(id = R.string.client_name_label)
                        )
                    },
                    label = {
                        Text(text = stringResource(id = R.string.client_name_label))
                    }
                )

                ImportContactButton(
                    modifier = Modifier.constrainAs(button) {
                        linkTo(textField.end,button.start, bias = 0f)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(textField.top)
                    }
                ) { name, phone ->
                    clientName = name
                    phoneNumber = phone
                }
            }

            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                value = description,
                onValueChange = {
                    description = it
                },
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

            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                },
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
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

            LazyRow() {
                item {
                    Spacer(modifier = Modifier.width(24.dp))
                }
                item {
                    AddServiceCard(modifier = Modifier.width(180.dp)) {
                        navigateToAddService()
                    }
                }
                items(services) { service ->
                    ServiceCard(
                        service = service,
                        modifier = Modifier.width(180.dp),
                        border = if (selectedService == service) {
                            BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        selectedService = service
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    FloatActionBar(
        canDelete = isEditing,
        delete = {
            delete()
            navigateToBack()
        },
        save = {
            val calendar = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
            }

            if (selectedService != null) {
                val saveRecord = Record(
                    clientName = if (clientName == "") null else clientName,
                    time = listOf(calendar.time),
                    description = if (description == "") null else description,
                    phone = if (phoneNumber == "") null else phoneNumber,
                    service = selectedService!!,
                    id = if (isEditing) record?.id ?: UUID.randomUUID() else UUID.randomUUID()
                )
                if (isEditing) {
                    edit(saveRecord)
                } else {
                    save(saveRecord)
                }
                navigateToBack()
            }
        }
    )


}

@Composable
private fun FloatActionBar(
    canDelete: Boolean,
    delete: () -> Unit,
    save: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
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
}

@Composable
private fun RecordDate(date: Date) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {

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
private fun ImportContactButton(modifier: Modifier = Modifier, import: (name: String, phone: String) -> Unit) {
    var importContact by remember { mutableStateOf(false) }
    val contentResolver = LocalContext.current.findActivity().contentResolver
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) {
        val contactFields = arrayOf(
            Contacts.DISPLAY_NAME,
            Contacts._ID,
        )
        it?.let { uri ->
            val cursor = contentResolver.query(uri, contactFields, null, null, null)
                ?: return@rememberLauncherForActivityResult
            cursor.use { cur ->
                if (cur.count == 0) return@rememberLauncherForActivityResult
                cur.moveToFirst()
                val name = cur.getString(0)
                val contactId = cur.getString(1)
                val phonesFields = arrayOf(Phone.NUMBER)
                val phones = contentResolver.query(Phone.CONTENT_URI, phonesFields,
                    Phone.CONTACT_ID + " = " + contactId,
                    null, null
                ) ?: return@rememberLauncherForActivityResult
                phones.use { ph ->
                    ph.moveToFirst()
                    val phone = ph.getString(0)
                    import(name, phone)
                }
            }

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
    } else {
        onGranted()
    }
}


