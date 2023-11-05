package com.murzify.meetum.core.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date
import java.util.UUID

@Serializable
data class Record(
    val clientName: String? = null,
    val time: List<@Serializable(with = DateSerializer::class) Date>,
    val description: String? = null,
    val phone: String? = null,
    val service: Service,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)

object DateSerializer: KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Date {
        val long = decoder.decodeLong()
        return Date(long)
    }

    override fun serialize(encoder: Encoder, value: Date) {
        val long = value.time
        encoder.encodeLong(long)
    }

}