package com.murzify.meetum.feature.calendar.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.murzify.meetum.feature.calendar.components.CalendarComponent

@Composable
fun CalendarUi(
    component: CalendarComponent,
    animOrientation: Orientation
) {
    val childStack by component.childStack.collectAsState()

    Children(
        childStack,
        animation = stackAnimation(slide(orientation = animOrientation))
    ) { child ->
        when (val instance = child.instance) {
            is CalendarComponent.Child.AddRecord -> AddRecordUi(instance.component)
            is CalendarComponent.Child.RecordInfo -> RecordInfoUi(instance.component)
            is CalendarComponent.Child.RecordsManager -> RecordsManagerUi(instance.component)
            is CalendarComponent.Child.RepetitiveEvents -> RepetitiveEventsUi(instance.component)
        }
    }
}