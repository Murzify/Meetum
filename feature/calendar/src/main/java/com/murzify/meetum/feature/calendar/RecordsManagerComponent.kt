package com.murzify.meetum.feature.calendar

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface RecordsManagerComponent {

    val currentRecords: StateFlow<List<Record>>
    val services: StateFlow<List<Service>>
    val allRecords: StateFlow<List<Record>>
    val selectedDate: StateFlow<LocalDate>

    fun onDateClick(date: LocalDate)

    fun onAddRecordClick()

    fun onRecordClick()
}