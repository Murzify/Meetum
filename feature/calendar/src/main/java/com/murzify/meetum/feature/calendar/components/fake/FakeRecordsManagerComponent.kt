package com.murzify.meetum.feature.calendar.components.fake

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.util.Currency
import java.util.Date

class FakeRecordsManagerComponent : RecordsManagerComponent {
    override val model: StateFlow<Model> = MutableStateFlow(
        Model(
            currentRecords = listOf(
                Record(
                    clientName = "Test",
                    time = listOf(Date()),
                    description = "test test test",
                    service = Service(
                        name = "test service",
                        price = 100.0,
                        currency = Currency.getInstance("USD")
                    )
                )
            ),
            services = listOf(),
            allRecords = emptyList(),
            selectedDate = LocalDate.now()
        )
    )

    override fun onDateClick(date: LocalDate) {}

    override fun onAddRecordClick() {}

    override fun onRecordClick(record: Record) {}

    override fun onDismissToStart(record: Record) {}
}