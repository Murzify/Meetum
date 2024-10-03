package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory

fun ComponentFactory.createResetPasswordComponent(
    componentContext: ComponentContext,
    navigateToSingIn: () -> Unit
): ResetPasswordComponent = RealResetPasswordComponent(
    componentContext,
    navigateToSingIn
)

class RealResetPasswordComponent(
    componentContext: ComponentContext,
    val navigateToSingIn: () -> Unit
) : ComponentContext by componentContext, ResetPasswordComponent {
    override fun onSignInClick() {
        navigateToSingIn()
    }

}