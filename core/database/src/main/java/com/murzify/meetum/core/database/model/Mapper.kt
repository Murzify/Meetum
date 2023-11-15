package com.murzify.meetum.core.database.model

import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service

fun Record.toEntity() = Records(
    id.toString(),
    clientName,
    description,
    phone,
    service.id.toString()
)

fun Service.toEntity() = Services(
    id.toString(), name, price, currency.currencyCode
)
