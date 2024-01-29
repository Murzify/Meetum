package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LookupRequest(
    val idToken: String
)
