package com.murzify.meetum.core.domain.model

import com.benasher44.uuid.Uuid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UUIDSerializer: KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uuid {
        val string = decoder.decodeString()
        return Uuid.fromString(string)
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        val string = value.toString()
        encoder.encodeString(string)
    }

}