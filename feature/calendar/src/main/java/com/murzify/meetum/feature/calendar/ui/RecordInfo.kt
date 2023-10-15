package com.murzify.meetum.feature.calendar.ui

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.ui.localStyleForeignFormat
import com.murzify.meetum.feature.calendar.CalendarViewModel
import com.murzify.ui.R
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun RecordInfoRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    navigateToEdit: (date: Date) -> Unit,
    navigateToBack: () -> Unit
) {
    val selectedRecord by viewModel.selectedRecord.collectAsState()
    RecordInfo(record = selectedRecord, navigateToEdit, navigateToBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordInfo(
    record: Record?,
    navigateToEdit: (Date) -> Unit,
    navigateToBack: () -> Unit
) {
    if (record == null) {
        return
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    RecordDateTime(date = record.time[0])
                },
                navigationIcon = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = { navigateToBack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = stringResource(id = R.string.back_button)
                        )
                    }
                },
                actions = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = { navigateToEdit(record.time[0]) }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = com.murzify.meetum.feature.calendar.R.drawable.round_edit_24
                            ),
                            contentDescription = stringResource(
                                id = com.murzify.meetum.feature.calendar.R.string.edit
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(),
            contentPadding = PaddingValues(
                top = it.calculateTopPadding(),
                start = 16.dp,
                end = 16.dp
            )
        ) {
            record.clientName?.let {
                item() {
                    InfoField(
                        iconId = com.murzify.meetum.feature.calendar.R.drawable.round_person_24,
                        contentDescriptionId = com.murzify.meetum.feature.calendar.R.string.client_name_label,
                        text = it
                    )
                }
            }
            record.phone?.let { phone ->
                item {
                    val context = LocalContext.current
                    InfoField(
                        iconId = com.murzify.meetum.feature.calendar.R.drawable.round_phone_24,
                        contentDescriptionId = com.murzify.meetum.feature.calendar.R.string.phone_label,
                        text = phone
                    ) {
                        val uri = "tel:$phone".toUri()
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = uri
                        context.startActivity(intent)
                    }
                }
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        painter = painterResource(id = R.drawable.round_handshake_24),
                        contentDescription = stringResource(id =
                        com.murzify.meetum.feature.calendar.R.string.service_label
                        ),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = record.service.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 24.sp
                        )
                    }
                    val format = localStyleForeignFormat(Locale.getDefault())
                    format.currency = record.service.currency
                    val price = format.format(record.service.price)

                    Text(
                        text = price,
                        modifier = Modifier.padding(start = 2.dp),
                        fontSize = 24.sp
                    )
                }
                HorizontalDivider()
            }
            record.description?.let {
                item() {
                    InfoField(
                        iconId = com.murzify.meetum.feature.calendar.R.drawable.round_description_24,
                        contentDescriptionId = com.murzify.meetum.feature.calendar.R.string.description_label,
                        text = it
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoField(
    @DrawableRes iconId: Int,
    @StringRes contentDescriptionId: Int,
    text: String,
    onLongPress: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            }
    ) {
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = contentDescriptionId)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 24.sp
        )
    }
    HorizontalDivider()
}

@Composable
private fun RecordDateTime(date: Date) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {

        val dateFormat = DateFormat.getDateTimeInstance(
            DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()
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
