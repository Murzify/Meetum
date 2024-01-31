package com.murzify.meetum.feature.auth.components

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface RegisterComponent {

    val model: StateFlow<Model>

    fun onEmailChange(email: String)

    fun onPasswordChange(password: String)

    fun onConfirmPasswordChange(confirmPassword: String)

    fun onRegisterClick()

    fun onSignInClick()

    @Serializable
    data class Model(
        val email: String,
        val password: String,
        val confirmPassword: String,
        val error: Error?,
        val loading: Boolean
    )

    enum class Error {
        DIFFERENT_PASSWORDS,
        SHORT_PASSWORD,
        INCORRECT_EMAIL,
        EMAIL_EXISTS
    }

}
