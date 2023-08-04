package com.murzify.meetum.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecordsList(records: List<Record>) {

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
        LazyColumn(state = listState) {
            items(records) {
                RecordCard(it)
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
                onClick = { }
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
fun RecordCard(record: Record = Record("Misha", "12:00", "massage")) {
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
            Column(Modifier.weight(1f)) {
                Text(text = record.username, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = record.serviceName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(text = record.time, modifier = Modifier.padding(start = 2.dp))
        }


    }
}

data class Record(val username: String, val time: String, val serviceName: String)