package com.murzify.meetum.core.database

import androidx.room.TypeConverter
import java.util.Currency
import java.util.Date
import java.util.UUID

class Converters {

    @TypeConverter
    fun fromUUID(value: UUID): String = value.toString()

    @TypeConverter
    fun toUUID(value: String): UUID = UUID.fromString(value)

    @TypeConverter
    fun fromCurrency(value: Currency): String = value.currencyCode

    @TypeConverter
    fun toCurrency(value: String): Currency = Currency.getInstance(value)

    @TypeConverter
    fun fromDate(value: Date): Long = value.time

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)
}