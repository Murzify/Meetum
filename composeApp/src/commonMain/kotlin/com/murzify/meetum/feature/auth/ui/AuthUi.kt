package com.murzify.meetum.feature.auth.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.murzify.meetum.feature.auth.components.AuthComponent

@Composable
fun AuthUi(
    component: AuthComponent,
    animOrientation: Orientation,
) {
    val childStack by component.childStack.collectAsState()

    Children(
        childStack,
        animation = stackAnimation(slide(orientation = animOrientation))
    ) { child ->
        when (val instance = child.instance) {
            is AuthComponent.Child.Register -> RegisterUi(instance.component)
            is AuthComponent.Child.SignIn -> SignInUi(instance.component)
        }
    }

}