package com.murzify.meetum.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.GregorianCalendar
import java.util.Locale

val serviceExample = Service(
    "Massage",
    200.toDouble(),
    Currency.getInstance("rub")
)

val recordExample = Record(
    "Misha",
    GregorianCalendar(2023, 7, 6, 16, 0).time,
    null,
    serviceExample
)



@Composable
fun RecordsList(
    records: List<Record>, addRecord: () -> Unit,
    topContent: @Composable () -> Unit
) {
    val listState = rememberLazyListState()

    val fabVisibility = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { topContent() }
            items(records) {
                RecordCard(it)
            }
            item {
                Spacer(
                    modifier = Modifier.height(64.dp)
                )
            }
        }

        val density = LocalDensity.current
        AnimatedVisibility(
            visible = fabVisibility.value,
            enter = slideInVertically {
                with(density) { 40.dp.roundToPx() }
            } + fadeIn(),
            exit = fadeOut(
                animationSpec = keyframes {
                    this.durationMillis = 120
                }
            )
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                onClick = { addRecord() }
            ) {
                Icon(
                    painter = painterResource(id = com.murzify.ui.R.drawable.round_add_24),
                    contentDescription = stringResource(id = R.string.add_record)
                )
            }
        }
    }


}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RecordCard(record: Record = recordExample) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (!record.clientName.isNullOrEmpty()) {
                    Text(
                        text = record.clientName!!,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(text = record.service.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

            Text(text = sdf.format(record.time), modifier = Modifier.padding(start = 2.dp))
        }
    }
}
