package com.murzify.meetum.core.domain.model

import com.benasher44.uuid.Uuid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Currency
import java.util.UUID

@Serializable
data class Service(
    val name: String,
    val price: Double,
    @Serializable(with = CurrencySerializer::class)
    val currency: Currency,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = Uuid.randomUUID()
)

object CurrencySerializer: KSerializer<Currency> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Currency", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Currency {
        val string = decoder.decodeString()
        return Currency.getInstance(string)
    }

    override fun serialize(encoder: Encoder, value: Currency) {
        val string = value.currencyCode
        encoder.encodeString(string)
    }

}
