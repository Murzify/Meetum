package com.murzify.meetum.core.domain.model

import java.util.Date
import java.util.UUID

data class Record(
    val clientName: String?,
    val time: Date,
    val description: String?,
    val service: Service,
    val id: UUID = UUID.randomUUID()
)