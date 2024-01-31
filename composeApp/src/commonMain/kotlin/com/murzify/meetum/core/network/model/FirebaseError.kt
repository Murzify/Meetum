package com.murzify.meetum.core.network.model

import kotlinx.serialization.Serializable


@Serializable
data class FirebaseError(
    val error: ErrorContent
)

@Serializable
data class ErrorContent(
    val code: Int,
    val message: String
)
