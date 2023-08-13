package com.murzify.meetum.feature.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.ui.AddServiceCard
import com.murzify.meetum.core.ui.ServiceCard
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
internal fun AddRecordRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    isEditing: Boolean,
    date: Date,
    navigateToBack: () -> Unit,
    navigateToAddService: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    AddRecordScreen(
        services,
        navigateToAddService,
        navigateToBack,
        viewModel::deleteRecord,
        viewModel::addRecord,
        isEditing,
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
    isEditing: Boolean,
    date: Date
) {
    val defCalendar = Calendar.getInstance().apply {
        time = Date()
        add(Calendar.HOUR_OF_DAY, 1)
        set(Calendar.MINUTE, 0)
    }

    val timePickerState = remember {
        TimePickerState(
            initialHour = defCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = defCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )
    }
    var selectedService: Service? by remember { mutableStateOf(null) }
    var clientName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        RecordDate(date = date)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TimeInput(
                modifier = Modifier,
                state = timePickerState
            )
        }

        OutlinedTextField(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            value = clientName,
            onValueChange = {
                clientName = it
            },
            label = {
                Text(text = stringResource(id = R.string.client_name_label))
            }
        )
        OutlinedTextField(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text(text = stringResource(id = R.string.description_label))
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
    }

    FloatActionBar(
        canDelete = isEditing,
        delete = {
            delete()
            navigateToBack()
        },
        save = {
            val cal = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
            }
            if (selectedService != null) {
                val record = Record(
                    clientName = clientName,
                    time = cal.time,
                    description = description,
                    service = selectedService!!
                )
                save(record)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,
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


