package com.murzify.meetum.feature.calendar.ui.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.round_delete_outline_24
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import org.jetbrains.compose.resources.painterResource

@Composable
fun BookingSwipe(
    currentRecord: RecordsManagerComponent.CurrentRecord,
    onClick: () -> Unit,
    onSwiped: () -> Unit,
) {
    var show by remember {
        mutableStateOf(true)
    }
    val dismissSate = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart ||
                it == SwipeToDismissBoxValue.StartToEnd
            ) {
                show = false
                onSwiped()

                true
            } else false
        }
    )
    Column(modifier = Modifier) {
        SwipeToDismissBox(
            state = dismissSate,
            backgroundContent = {
                DismissBackground(dismissSate = dismissSate)
            },
            content = {
                BookingCard(
                    currentRecord.record,
                    currentRecord.time,
                    onClick = onClick
                )
            },
        )
        HorizontalDivider()
    }
}

@Composable
private fun DismissBackground(dismissSate: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        MaterialTheme.colorScheme.errorContainer, label = ""
    )
    val contentAlignment = when (dismissSate.targetValue) {
        SwipeToDismissBoxValue.Settled -> Alignment.Center
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize()
            .background(color = color),
        contentAlignment = contentAlignment
    ) {
        Icon(
            painter = painterResource(Res.drawable.round_delete_outline_24),
            contentDescription = "",
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}