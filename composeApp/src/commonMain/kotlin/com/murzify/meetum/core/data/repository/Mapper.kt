package com.murzify.meetum.core.data.repository

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import kotlinx.datetime.Instant
import java.util.Currency

fun List<FullRecord>.mapToRecord() = groupBy { it.recordIdDate }
    .map { (id, records) ->
        val record = records.first()
        Record(
            clientName = record.clientName,
            time = records.map { Instant.fromEpochMilliseconds(it.date) },
            description = record.description,
            phone = record.phone,
            service = Service(
                name = record.name,
                price = record.price,
                currency = Currency.getInstance(record.currency),
                id = Uuid.fromString(record.serviceIdService)
            ),
            id = Uuid.fromString(id),
        )
    }