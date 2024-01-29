package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordAuth(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)
