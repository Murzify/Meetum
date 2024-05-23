package com.murzify.meetum.core.domain.model

import com.benasher44.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class Record(
    val clientName: String? = null,
    val dates: List<RecordTime>,
    val description: String? = null,
    val phone: String? = null,
    val service: Service,
    @Serializable(with = UUIDSerializer::class)
    val id: Uuid = Uuid.randomUUID()
)