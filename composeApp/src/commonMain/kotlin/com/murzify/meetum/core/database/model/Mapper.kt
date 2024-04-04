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
    service.id.toString(),
    deleted = false,
    synced = false
)

fun Service.toEntity(synced: Boolean) = Services(
    id.toString(),
    name,
    price,
    currency.currencyCode,
    deletion = false,
    synced = synced
)

fun RecordTime.toEntity(recordId: Uuid) = Record_dates(
    id.toString(),
    recordId.toString(),
    time.toEpochMilliseconds(),
    deleted = false,
    synced = false
)
