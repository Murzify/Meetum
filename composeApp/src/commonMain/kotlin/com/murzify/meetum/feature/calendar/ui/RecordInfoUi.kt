package com.murzify.meetum.feature.calendar.ui

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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.priceFormat
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.text.DateFormat
import java.util.Date
import java.util.Locale


@Composable
expect fun onPhoneLongClick(model: RecordInfoComponent.Model)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
internal fun RecordInfoUi(
    component: RecordInfoComponent
) {
    val model by component.model.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    RecordDateTime(localDateTime = model.date.toLocalDateTime(TimeZone.currentSystemDefault()))
                },
                navigationIcon = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = component::onBackClick
                    ) {
                        Icon(
                            painter = painterResource("drawable/round_arrow_back_24.xml"),
                            contentDescription = stringResource(MR.strings.back_button)
                        )
                    }
                },
                actions = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = component::onEditClick
                    ) {
                        Icon(
                            painter = painterResource(
                                "drawable/round_edit_24.xml"
                            ),
                            contentDescription = stringResource(
                                MR.strings.edit
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
            model.apply {
                record.clientName?.let {
                    item {
                        InfoField(
                            iconPath = "drawable/round_person_24.xml",
                            contentDescriptionId = MR.strings.client_name_label,
                            text = it
                        )
                    }
                }
                record.phone?.let { phone ->
                    item {
                        var phoneClick by mutableStateOf(false)
                        InfoField(
                            iconPath = "drawable/round_phone_24.xml",
                            contentDescriptionId = MR.strings.phone_label,
                            text = phone,
                            onLongPress = { phoneClick = true }
                        )
                        if (phoneClick) {
                            onPhoneLongClick(this@apply)
                            phoneClick = false
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
                            painter = painterResource("drawable/round_handshake_24.xml"),
                            contentDescription = stringResource(MR.strings.service_label),
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
                        val format = priceFormat(
                            Locale.getDefault(),
                            record.service.currency
                        )
                        val price = format.format(record.service.price)

                        Text(
                            text = price,
                            modifier = Modifier.padding(start = 2.dp),
                            fontSize = 24.sp
                        )
                    }
                    Divider()
                }
                record.description?.let {
                    item {
                        InfoField(
                            iconPath = "drawable/round_description_24.xml",
                            contentDescriptionId = MR.strings.description_label,
                            text = it
                        )
                    }
                }
                item {
                    SocialBar(record)
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun InfoField(
    iconPath: String,
    contentDescriptionId: StringResource,
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
            painter = painterResource(iconPath),
            contentDescription = stringResource(contentDescriptionId)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 24.sp
        )
    }
    Divider()
}

@Composable
private fun RecordDateTime(localDateTime: LocalDateTime) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {

        val dateFormat = DateFormat.getDateTimeInstance(
            DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()
        )
        val date = Date.from(
            localDateTime
                .toInstant(TimeZone.currentSystemDefault())
                .toJavaInstant()
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
