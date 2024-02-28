package com.murzify.meetum.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseService(
    val name: String,
    val price: Double,
    val currency: String,
)
