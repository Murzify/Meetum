package com.murzify.meetum.feature.calendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.slot.ChildSlot
import com.murzify.meetum.core.ui.isMediumWindow
import com.murzify.meetum.feature.calendar.components.CalendarComponent
import com.murzify.meetum.feature.calendar.components.CalendarComponent.Child
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent

@Composable
fun CalendarUi(
    component: CalendarComponent,
    animOrientation: Orientation
) {
    val childStack by component.childStack.collectAsState()
    val childSlot by component.childSlot.collectAsState()
    component.isMediumWindow = isMediumWindow()

    Children(
        childStack,
        animation = stackAnimation(slide(orientation = animOrientation))
    ) { child ->
        when (val instance = child.instance) {
            is Child.AddRecord -> AddRecordUi(instance.component)
            is Child.RecordInfo -> RecordInfoUi(instance.component)
            is Child.RecordsManager -> if (isMediumWindow()) {
                ExpandedRecordsManager(instance.component, childSlot)
            } else RecordsManagerUi(instance.component)
            is Child.RepetitiveEvents -> RepetitiveEventsUi(instance.component)
        }
    }
}

@Composable
private fun ExpandedRecordsManager(
    recordsManagerComponent: RecordsManagerComponent,
    childSlot: ChildSlot<*, Child>,
) {
    Row {
        Box(Modifier.weight(1f)) {
            RecordsManagerUi(recordsManagerComponent)
        }
        AnimatedVisibility(
            visible = childSlot.child != null,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearEasing
                )
            ),
            exit = fadeOut(),
            modifier = Modifier.width(330.dp)
        ) {
            ElevatedCard(
                Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                childSlot.child?.instance?.also {
                    when (it) {
                        is Child.AddRecord -> AddRecordUi(it.component)
                        is Child.RecordInfo -> RecordInfoUi(it.component)
                        is Child.RepetitiveEvents -> RepetitiveEventsUi(it.component)
                        else -> Unit
                    }
                }
            }
        }

    }
}