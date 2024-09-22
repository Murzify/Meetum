package com.murzify.meetum.core.domain.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RecordTime(
    @Serializable(with = UUIDSerializer::class)
    val id: Uuid = Uuid.randomUUID(),
    val time: Instant
)
