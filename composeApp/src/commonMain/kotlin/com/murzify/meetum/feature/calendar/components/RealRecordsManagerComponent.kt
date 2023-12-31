package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent.Model
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.koin.core.component.get


fun ComponentFactory.createRecordsManagerComponent(
    componentContext: ComponentContext,
    navigateToAddRecord: (date: Instant, record: Record?) -> Unit,
    navigateToRecordInfo: (record: Record, date: Instant) -> Unit,
): RecordsManagerComponent {
    return RealRecordsManagerComponent(
        componentContext,
        navigateToAddRecord,
        navigateToRecordInfo,
        get(),
        get(),
        get()
    )
}

class RealRecordsManagerComponent constructor (
    componentContext: ComponentContext,
    private val navigateToAddRecord: (date: Instant, record: Record?) -> Unit,
    private val navigateToRecordInfo: (record: Record, date: Instant) -> Unit,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepository: RecordRepository,
    private val getRecordsUseCase: GetRecordsUseCase,
): ComponentContext by componentContext, RecordsManagerComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            currentRecords = emptyList(),
            services = emptyList(),
            allRecords = emptyList(),
            selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
    )

    private val coroutineScope = componentCoroutineScope()

    init {
        registerKeeper(Model.serializer()) { model.value }
        coroutineScope.launch(meetumDispatchers.io) {
            getServicesUseCase()
                .collect { services ->
                    model.update { it.copy(services = services) }
                }
        }

        coroutineScope.launch(meetumDispatchers.io) {
            recordRepository.getAllRecords().collect { allRecords ->
                model.update { it.copy(allRecords = allRecords) }
            }
        }

        coroutineScope.launch(meetumDispatchers.io) {
            var currentRecordsJob: Job? = null

            model.map { it.selectedDate }.collect { selectedDate ->
                currentRecordsJob?.cancel()
                currentRecordsJob = launch {
                    getRecordsUseCase(selectedDate.toInstant()).collect { currentRecords ->
                        val new = currentRecords.map {
                            it.copy(time = listOf(getSelectedDate(it)))
                        }
                        model.update { it.copy(currentRecords = new) }
                    }
                }
            }
        }
    }

    override fun onDateClick(date: LocalDate) {
        model.update { it.copy(selectedDate = date) }
        coroutineScope.launch(meetumDispatchers.io) {
            getRecordsUseCase(date.toInstant()).collect { currentRecords ->
                model.update { it.copy(currentRecords = currentRecords) }
            }
        }
    }

    override fun onAddRecordClick() {
        val tz = TimeZone.currentSystemDefault()
        val currentTime = Clock.System.now().toLocalDateTime(tz).time
        val selectedDate = model.value.selectedDate
        val selectedDateTime = LocalDateTime(selectedDate, currentTime)
        navigateToAddRecord(selectedDateTime.toInstant(tz), null)
    }

    override fun onRecordClick(record: Record) {
        val localDateTime = getSelectedDate(record)
        navigateToRecordInfo(record, localDateTime)
    }

    override fun onDismissToStart(record: Record) {
        val selectedDate = getSelectedDate(record)
        coroutineScope.launch(meetumDispatchers.io) {
            model.update {
                val records = it.currentRecords.toMutableList()
                records.remove(record)
                it.copy(currentRecords = records)
            }
            recordRepository.deleteDate(record.id, selectedDate)
        }
    }

    private fun getSelectedDate(record: Record): Instant {
        val tz = TimeZone.currentSystemDefault()
        val recordTime = record.time.first().toLocalDateTime(tz).time
        val selectedDate = model.value.selectedDate
        return LocalDateTime(selectedDate, recordTime).toInstant(tz)
    }

    private fun LocalDate.toInstant(): Instant {
        val localTime = LocalTime(0, 0, 0, 0)
        return LocalDateTime(this, localTime).toInstant(TimeZone.currentSystemDefault())
    }

}