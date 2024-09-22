package com.murzify.meetum.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val localId: String,
    val email: String,
    val passwordHash: String,
    val emailVerified: Boolean,
    val passwordUpdatedAt: Long,
    val validSince: String,
    val lastLoginAt: String,
    val createdAt: String,
    val lastRefreshAt: String
)
