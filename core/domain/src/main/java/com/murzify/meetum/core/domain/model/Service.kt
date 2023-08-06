package com.murzify.meetum.core.domain.model

import java.util.Currency
import java.util.UUID

data class Service(
    val name: String,
    val price: Double,
    val currency: Currency,
    val id: UUID = UUID.randomUUID()
)
