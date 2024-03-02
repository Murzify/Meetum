package com.murzify.meetum.core.network

import Meetum.composeApp.BuildConfig
import com.murzify.meetum.core.network.model.EmailPasswordAuth
import com.murzify.meetum.core.network.model.EmailVerification
import com.murzify.meetum.core.network.model.LookupRequest
import com.murzify.meetum.core.network.model.ResetPassword
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun firebaseHttpClient() = HttpClient {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Napier.v(message, null, "Ktor")
            }
        }
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
        url {
            url("https://identitytoolkit.googleapis.com/v1/")
            parameters.append("key", BuildConfig.API_KEY)
        }
    }
}

class FirebaseAuth(private val httpClient: HttpClient) {

    suspend fun  createUser(email: String, password: String) = httpClient
        .post {
            url { path("accounts:signUp") }
            setBody(EmailPasswordAuth(email, password))
        }

    suspend fun sendEmailVerification(idToken: String) = httpClient
        .post {
            url {
                path("accounts:sendOobCode")
            }
            setBody(EmailVerification(idToken = idToken, requestType = "VERIFY_EMAIL"))
        }

    suspend fun lookup(idToken: String) = httpClient
        .post {
            url {
                path("accounts:lookup")
            }
            setBody(LookupRequest(idToken))
        }

    suspend fun resetPassword(email: String) = httpClient
        .post {
            url {
                path("accounts:sendOobCode")
            }
            setBody(ResetPassword(email = email, requestType = "PASSWORD_RESET"))
        }
}

