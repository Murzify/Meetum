package com.murzify.meetum.core.domain.model

import java.util.Date
import java.util.UUID

data class Record(
    val clientName: String? = null,
    val time: Date,
    val description: String? = null,
    val phone: String? = null,
    val service: Service,
    val id: UUID = UUID.randomUUID()
)