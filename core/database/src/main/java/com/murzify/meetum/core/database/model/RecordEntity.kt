package com.murzify.meetum.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.murzify.meetum.core.domain.model.Record
import java.util.Date
import java.util.UUID

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey @ColumnInfo(name = "record_id") val recordId: UUID,
    @ColumnInfo(name = "client_name") val clientName: String?,
    @ColumnInfo(name = "time") val time: Date,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "service_id") val serviceId: UUID
)

fun Record.toEntity() = RecordEntity(
    id,
    clientName,
    time,
    description,
    phone,
    service.id
)
