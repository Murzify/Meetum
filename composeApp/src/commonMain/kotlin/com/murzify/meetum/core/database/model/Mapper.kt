package com.murzify.meetum.core.database.model

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
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

fun RecordTime.toEntity(recordId: Uuid) = Record_dates(
    id.toString(),
    recordId.toString(),
    time.toEpochMilliseconds()
)
