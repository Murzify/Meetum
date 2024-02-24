package com.murzify.meetum.feature.calendar.components.fake

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.Currency
import java.util.UUID

class FakeRecordsManagerComponent : RecordsManagerComponent {
    override val model: StateFlow<Model> = MutableStateFlow(
        Model(
            currentRecords = listOf(
                Record(
                    clientName = "Test",
                    dates = listOf(
                        RecordTime(
                            id = UUID.randomUUID(),
                            time = Clock.System.now()
                        )
                    ),
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
            selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
    )

    override fun onDateClick(date: LocalDate) {}

    override fun onAddRecordClick() {}

    override fun onRecordClick(record: Record, recordTime: RecordTime) {}

    override fun onDismissToStart(record: Record, recordTime: RecordTime) {}
}