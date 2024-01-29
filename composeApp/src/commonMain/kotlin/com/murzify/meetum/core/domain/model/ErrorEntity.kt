package com.murzify.meetum.core.domain.model

import io.sentry.kotlin.multiplatform.Sentry

sealed class ErrorEntity : Throwable() {
    class EmailExists: ErrorEntity()

    class InvalidLogin: ErrorEntity()

    class InvalidIdToken: ErrorEntity()

    data class Unknown(override val message: String): ErrorEntity()

    companion object {
        operator fun get(message: String): ErrorEntity {
            return when (message) {
                "EMAIL_EXISTS" -> EmailExists()
                "INVALID_LOGIN_CREDENTIALS" -> InvalidLogin()
                "INVALID_ID_TOKEN" -> InvalidIdToken()
                else -> {
                    Sentry.captureMessage(message)
                    Unknown(message)
                }
            }
        }
    }

}

