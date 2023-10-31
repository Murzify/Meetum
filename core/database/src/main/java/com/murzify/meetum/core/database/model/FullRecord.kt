package com.murzify.meetum.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.murzify.meetum.core.domain.model.Record

data class FullRecord(
    @Embedded val record: RecordEntity,
    @Relation(parentColumn = "service_id", entityColumn = "service_id") val service: ServiceEntity,
    @Relation(parentColumn = "record_id", entityColumn = "record_id") val dates: List<RecordDatesEntity>
)

fun FullRecord.toDomain() = Record(
    record.clientName,
    dates.map { it.date },
    record.description,
    record.phone,
    service.toDomain(),
    record.recordId
)

