package com.murzify.meetum.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.domain.model.Service
import java.util.Currency
import java.util.UUID

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey @ColumnInfo(name = "service_id") val serviceId: UUID,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "currency") val currency: Currency,
)

fun Service.toEntity() = Services(
    id.toString(), name, price, currency.currencyCode
)
