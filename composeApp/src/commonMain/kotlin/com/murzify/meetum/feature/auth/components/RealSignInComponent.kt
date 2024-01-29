package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory

fun ComponentFactory.createSignInComponent(
    componentContext: ComponentContext,
    navigateToRegister: () -> Unit,
): SignInComponent = RealSignInComponent(
    componentContext,
    navigateToRegister
)

class RealSignInComponent(
    componentContext: ComponentContext,
    private val navigateToRegister: () -> Unit,
): ComponentContext by componentContext, SignInComponent {
    override fun onEmailChange(email: String) {
        TODO("Not yet implemented")
    }

    override fun onPasswordChange(password: String) {
        TODO("Not yet implemented")
    }

    override fun onSignInClick() {
        TODO("Not yet implemented")
    }

    override fun onRegisterClick() {
        TODO("Not yet implemented")
    }

    override fun onForgotPasswordClick() {
        TODO("Not yet implemented")
    }
}