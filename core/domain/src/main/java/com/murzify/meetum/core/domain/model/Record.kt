package com.murzify.meetum.core.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Record(
    val clientName: String? = null,
    val time: List<Instant>,
    val description: String? = null,
    val phone: String? = null,
    val service: Service,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)