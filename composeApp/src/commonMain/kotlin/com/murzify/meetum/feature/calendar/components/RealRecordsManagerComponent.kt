package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent.CurrentRecord
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent.Model
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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
    navigateToAddRecord: (recordTime: RecordTime, record: Record?) -> Unit,
    navigateToRecordInfo: (record: Record, recordTime: RecordTime) -> Unit,
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

class RealRecordsManagerComponent (
    componentContext: ComponentContext,
    private val navigateToAddRecord: (recordTime: RecordTime, record: Record?) -> Unit,
    private val navigateToRecordInfo: (record: Record, recordTime: RecordTime) -> Unit,
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
    private var loadRecordsJob: Job? = null

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


    }

    override fun onDateClick(date: LocalDate) {
        model.update { it.copy(selectedDate = date) }
        loadRecordsJob?.cancel()
        loadRecordsJob = coroutineScope.launch(meetumDispatchers.io) {
            getRecordsUseCase(date.toInstant()).collect { currentRecords ->
                val tz = TimeZone.currentSystemDefault()
                val filteredRecords = currentRecords.mapNotNull { record ->
                    record.dates.firstOrNull { recordTime ->
                        recordTime.time.toLocalDateTime(tz).date == date
                    }?.let {
                        return@mapNotNull CurrentRecord(
                            record,
                            it
                        )
                    }
                    return@mapNotNull null
                }
                model.update { it.copy(currentRecords = filteredRecords) }
            }
        }
    }

    override fun onAddRecordClick() {
        val tz = TimeZone.currentSystemDefault()
        val currentTime = Clock.System.now().toLocalDateTime(tz).time
        val selectedDate = model.value.selectedDate
        val selectedDateTime = LocalDateTime(selectedDate, currentTime)
        val recordTime = RecordTime(
            time = selectedDateTime.toInstant(tz)
        )
        navigateToAddRecord(recordTime, null)
    }

    override fun onRecordClick(record: Record, recordTime: RecordTime) {
        navigateToRecordInfo(record, recordTime)
    }

    override fun onDismissToStart(currentRecord: CurrentRecord) {
        coroutineScope.launch(meetumDispatchers.io) {
            model.update {
                val records = it.currentRecords.toMutableList()
                records.remove(currentRecord)
                it.copy(currentRecords = records)
            }
            recordRepository.deleteDate(currentRecord.time, currentRecord.record.id)
        }
    }

    private fun LocalDate.toInstant(): Instant {
        val localTime = LocalTime(0, 0, 0, 0)
        return LocalDateTime(this, localTime).toInstant(TimeZone.currentSystemDefault())
    }

}