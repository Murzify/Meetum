package com.murzify.meetum.core.network

import Meetum.composeApp.BuildConfig
import com.murzify.meetum.core.network.model.EmailPasswordAuth
import com.murzify.meetum.core.network.model.EmailVerification
import com.murzify.meetum.core.network.model.LookupRequest
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class FirebaseAuth() {
    private val httpClient = HttpClient {
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
}

