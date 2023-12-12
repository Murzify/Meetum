package com.murzify.meetum.feature.calendar.components

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

interface RecordsManagerComponent {
    val model: StateFlow<Model>

    fun onDateClick(date: LocalDate)

    fun onAddRecordClick()

    fun onRecordClick(record: Record)

    fun onDismissToStart(record: Record)

    @Serializable
    data class Model(
        val currentRecords: List<Record>,
        val services: List<Service>,
        val allRecords: List<Record>,
        val selectedDate: LocalDate
    )

}

