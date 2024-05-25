package com.murzify.meetum.core.data

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.model.FirebaseBooking
import com.murzify.meetum.core.data.model.FirebaseBookingTime
import com.murzify.meetum.core.data.model.FirebaseService
import com.murzify.meetum.core.database.GetUnsynced
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

fun Services.toFirebase() = FirebaseService(
    name,
    price,
    currency,
    deleted
)

fun Record.toFirebase() = FirebaseBooking(
    clientName,
    description,
    phone,
    service.id.toString(),
    dates.associate {
        it.id.toString() to FirebaseBookingTime(
            it.time.toEpochMilliseconds(),
            false
        )
    }
)

fun Flow<List<GetUnsynced>>.toFirebase() = map { entityList ->
    entityList.groupBy { it.record_id }.mapValues { (recordId, entityList) ->
        val first = entityList.first()
        val init = FirebaseBooking(
            first.client_name,
            first.description,
            first.phone,
            first.service_id,
            time = mutableMapOf(),
            first.deleted
        )
        entityList.fold(init) { acc, entity ->
            val map = acc.time.toMutableMap()
            map[entity.date_id] = FirebaseBookingTime(
                entity.date,
                entity.deleted_
            )
            acc.copy(
                time = map
            )
        }
    }
}