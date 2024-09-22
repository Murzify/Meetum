package com.murzify.meetum.feature.calendar.components

import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface RecordInfoComponent {

    val model: StateFlow<Model>

    fun onEditClick()

    fun onBackClick()

    @Serializable
    data class Model(
        val record: Record,
        val date: Instant
    )
}