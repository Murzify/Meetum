package com.murzify.meetum.core.data

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.model.Service
import kotlinx.datetime.Instant
import java.util.Currency

fun List<FullRecord>.mapToRecord() = groupBy { it.recordId }
    .map { (id, records) ->
        val record = records.first()
        Record(
            clientName = record.clientName,
            dates = records.map {
                RecordTime(Uuid.fromString(it.dateId),
                    Instant.fromEpochMilliseconds(it.date)
                )
            },
            description = record.description,
            phone = record.phone,
            service = Service(
                name = record.name,
                price = record.price,
                currency = Currency.getInstance(record.currency),
                id = Uuid.fromString(record.serviceId)
            ),
            id = Uuid.fromString(id),
        )
    }