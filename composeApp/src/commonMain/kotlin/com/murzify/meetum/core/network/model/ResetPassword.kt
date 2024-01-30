package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPassword(
    val requestType: String,
    val email: String
)
