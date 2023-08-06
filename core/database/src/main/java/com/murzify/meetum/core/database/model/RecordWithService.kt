package com.murzify.meetum.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.murzify.meetum.core.domain.model.Record

data class RecordWithService(
    @Embedded val record: RecordEntity,
    @Relation(parentColumn = "service_id", entityColumn = "service_id") val service: ServiceEntity
)

fun RecordWithService.toDomain() = Record(
    record.clientName,
    record.time,
    record.description,
    service.toDomain(),
    record.recordId
)

