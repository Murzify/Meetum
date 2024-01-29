package com.murzify.meetum.feature.auth.components

interface SignInComponent {

    fun onEmailChange(email: String)

    fun onPasswordChange(password: String)

    fun onSignInClick()

    fun onRegisterClick()

    fun onForgotPasswordClick()

    data class Model(
        val email: String,
        val password: String
    )

}
