package com.murzify.meetum.core.database.model

data class FullRecord(
    val record_id: String,
    val client_name: String?,
    val description: String?,
    val phone: String?,
    val service_id: String,
    val service_id_: String,
    val name: String,
    val price: Double,
    val currency: String,
    val date_id: String,
    val record_id_: String,
    val date: Long
)

