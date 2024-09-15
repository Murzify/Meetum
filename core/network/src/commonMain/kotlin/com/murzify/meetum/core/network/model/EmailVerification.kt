package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailVerification(
    val idToken: String,
    val requestType: String,
)
