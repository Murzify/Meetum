package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PasswordSignUpResult(
    val kind: String,
    val idToken: String,
    val email: String,
    val refreshToken: String,
    val expiresIn: String,
    val localId: String
)

