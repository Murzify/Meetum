package com.murzify.meetum.feature.auth.components

import kotlinx.coroutines.flow.StateFlow

interface SignInComponent {

    val model: StateFlow<Model>

    fun onEmailChange(email: String)

    fun onPasswordChange(password: String)

    fun onSignInClick()

    fun onRegisterClick()

    fun onForgotPasswordClick()

    data class Model(
        val email: String,
        val password: String,
        val loading: Boolean,
        val error: Error?
    )

    enum class Error {
        INVALID_CREDENTIALS,
        MISSING_EMAIL,
        INVALID_EMAIL
    }

}
