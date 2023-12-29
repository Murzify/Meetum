package com.murzify.meetum.feature.services.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.slot.ChildSlot
import com.murzify.meetum.core.ui.isMediumWindow
import com.murzify.meetum.feature.services.components.ServicesComponent
import com.murzify.meetum.feature.services.components.ServicesComponent.Child.AddService
import com.murzify.meetum.feature.services.components.ServicesComponent.Child.ServicesList
import com.murzify.meetum.feature.services.components.ServicesListComponent

@Composable
fun ServicesUi(component: ServicesComponent, animOrientation: Orientation) {
    val childStack by component.childStack.collectAsState()
    val childSlot by component.childSlot.collectAsState()

    component.isMediumWindow = isMediumWindow()
    Children(
        childStack,
        animation = stackAnimation(slide(orientation = animOrientation))
    ) { child ->
        when (val instance = child.instance) {
            is AddService -> AddServiceUi(instance.component)
            is ServicesList -> {
                if (isMediumWindow()) {
                    ExpandedServiceList(
                        instance.component,
                        childSlot
                    )
                } else {
                    ServicesListUi(instance.component)
                }
            }
        }
    }
}

@Composable
private fun ExpandedServiceList(
    servicesListComponent: ServicesListComponent,
    childSlot: ChildSlot<*, ServicesComponent.Child>
) {
    Row {
        Box(Modifier.weight(1f)) {
            ServicesListUi(servicesListComponent)
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
                    AddServiceUi((it as AddService).component)
                }
            }
        }

    }
}