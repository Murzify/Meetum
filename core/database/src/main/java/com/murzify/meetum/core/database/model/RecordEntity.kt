package com.murzify.meetum.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.domain.model.Record
import java.util.UUID

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey @ColumnInfo(name = "record_id") val recordId: UUID,
    @ColumnInfo(name = "client_name") val clientName: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "service_id") val serviceId: UUID
)

fun Record.toEntity() = Records(
    id.toString(),
    clientName,
    description,
    phone,
    service.id.toString()
)
