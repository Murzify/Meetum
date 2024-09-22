package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.core.ui.priceFormat
import com.murzify.meetum.core.ui.resources.*
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.*
import java.text.DateFormat
import java.util.*


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
                    IconButton(
                        modifier = Modifier
                            .padding(8.dp),
                        onClick = component::onBackClick
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.round_arrow_back_24),
                            contentDescription = stringResource(Res.string.back_button)
                        )
                    }
                },
                actions = {
                    IconButton(
                        modifier = Modifier
                            .padding(8.dp),
                        onClick = component::onEditClick
                    ) {
                        Icon(
                            painter = painterResource(
                                Res.drawable.round_edit_24
                            ),
                            contentDescription = stringResource(
                                Res.string.edit
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
                            res = Res.drawable.round_person_24,
                            contentDescriptionId = Res.string.client_name_label,
                            text = it
                        )
                    }
                }
                record.phone?.let { phone ->
                    item {
                        var phoneClick by mutableStateOf(false)
                        InfoField(
                            res = Res.drawable.round_phone_24,
                            contentDescriptionId = Res.string.phone_label,
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
                            painter = painterResource(Res.drawable.round_handshake_24),
                            contentDescription = stringResource(Res.string.service_label),
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
                    HorizontalDivider()
                }
                record.description?.let {
                    item {
                        InfoField(
                            res = Res.drawable.round_description_24,
                            contentDescriptionId = Res.string.description_label,
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
    res: DrawableResource,
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
            painter = painterResource(res),
            contentDescription = stringResource(contentDescriptionId)
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
