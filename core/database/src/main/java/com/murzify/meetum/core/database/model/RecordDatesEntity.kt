package com.murzify.meetum.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "record_dates",
    foreignKeys = [
        ForeignKey(
            entity = RecordEntity::class,
            parentColumns = ["record_id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecordDatesEntity (
    @PrimaryKey @ColumnInfo(name = "date_id") val dateId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "record_id") val recordId: UUID,
    @ColumnInfo(name = "date") val date: Date
)
