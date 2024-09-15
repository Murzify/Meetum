package com.murzify.meetum.core.network.model

import com.murzify.meetum.core.domain.model.FirebaseUser
import kotlinx.serialization.Serializable

@Serializable
data class LookupResponse(
    val kind: String,
    val users: List<FirebaseUser>
)
