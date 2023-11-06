package com.murzify.meetum.feature.services.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.murzify.meetum.feature.services.components.ServicesComponent

@Composable
fun ServicesUi(component: ServicesComponent, animOrientation: Orientation) {
    val childStack by component.childStack.collectAsState()

    Children(
        childStack,
        animation = stackAnimation(slide(orientation = animOrientation))
    ) { child ->
        when (val instance = child.instance) {
            is ServicesComponent.Child.AddService -> AddServiceUi(instance.component)
            is ServicesComponent.Child.ServicesList -> ServicesListUi(instance.component)
        }
    }
}