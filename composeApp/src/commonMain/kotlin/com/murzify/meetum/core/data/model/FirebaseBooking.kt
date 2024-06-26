package com.murzify.meetum.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseBooking(
    @SerialName("client_name")
    val clientName: String?,
    val description: String?,
    val phone: String?,
    @SerialName("service_id")
    val serviceId: String,
    val time: Map<String, FirebaseBookingTime> = emptyMap(),
    val deleted: Boolean = false
)

@Serializable
data class FirebaseBookingTime(
    val time: Long,
    val deleted: Boolean = false
)

