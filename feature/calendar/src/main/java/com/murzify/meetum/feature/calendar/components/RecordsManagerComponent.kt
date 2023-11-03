package com.murzify.meetum.feature.calendar.components

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface RecordsManagerComponent {
    val model: StateFlow<Model>

    fun onDateClick(date: LocalDate)

    fun onAddRecordClick()

    fun onRecordClick(record: Record)

    @Serializable
    data class Model(
        val currentRecords: List<Record>,
        val services: List<Service>,
        val allRecords: List<Record>,
        @Serializable(with = LocalDateSerializer::class)
        val selectedDate: LocalDate
    )

    private object LocalDateSerializer: KSerializer<LocalDate> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "LocalDate", PrimitiveKind.LONG
        )

        override fun deserialize(decoder: Decoder): LocalDate {
            val long = decoder.decodeLong()
            return Instant.ofEpochMilli(long).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        override fun serialize(encoder: Encoder, value: LocalDate) {
            val long = value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            encoder.encodeLong(long)
        }

    }
}

