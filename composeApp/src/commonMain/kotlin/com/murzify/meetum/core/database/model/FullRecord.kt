package com.murzify.meetum.core.database.model

data class FullRecord(
    val recordId: String,
    val clientName: String?,
    val description: String?,
    val phone: String?,
    val serviceId: String,
    val serviceIdService: String,
    val name: String,
    val price: Double,
    val currency: String,
    val dateId: String,
    val recordIdDate: String,
    val date: Long
)

