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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun ComponentFactory.createRecordsManagerComponent(
    componentContext: ComponentContext,
    navigateToAddRecord: (date: Date, record: Record?) -> Unit,
    navigateToRecordInfo: (record: Record, date: Date) -> Unit,
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
    private val navigateToAddRecord: (date: Date, record: Record?) -> Unit,
    private val navigateToRecordInfo: (record: Record, date:Date) -> Unit,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepository: RecordRepository,
    private val getRecordsUseCase: GetRecordsUseCase,
): ComponentContext by componentContext, RecordsManagerComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            currentRecords = emptyList(),
            services = emptyList(),
            allRecords = emptyList(),
            selectedDate = LocalDate.now()
        )
    )

    private val coroutineScope = componentCoroutineScope()

    init {
        registerKeeper(Model.serializer()) { model.value }
        coroutineScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect { services ->
                    model.update { it.copy(services = services) }
                }
        }

        coroutineScope.launch(Dispatchers.IO) {
            recordRepository.getAllRecords().collect { allRecords ->
                model.update { it.copy(allRecords = allRecords) }
            }
        }

        coroutineScope.launch(Dispatchers.IO) {
            getRecordsUseCase(model.value.selectedDate.toDate()).collect { currentRecords ->
                model.update { it.copy(currentRecords = currentRecords) }
            }
        }
    }

    override fun onDateClick(date: LocalDate) {
        val currentCal = Calendar.getInstance().apply { time = Date() }
        val time = Calendar.getInstance().apply {
            time = date.toDate()
            set(Calendar.HOUR, currentCal.get(Calendar.HOUR))
            set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE))
        }.time
        val newSelectedDate = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        model.update { it.copy(selectedDate = newSelectedDate) }
        coroutineScope.launch(Dispatchers.IO) {
            getRecordsUseCase(date.toDate()).collect { currentRecords ->
                model.update { it.copy(currentRecords = currentRecords) }
            }
        }
    }

    override fun onAddRecordClick() {
        val currentCalendar = Calendar.getInstance().apply {
            time = Date()
        }
        val selectedDate = Calendar.getInstance().apply {
            time = model.value.selectedDate.toDate()
            set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
        }.time
        navigateToAddRecord(selectedDate, null)
    }

    override fun onRecordClick(record: Record) {
        val recordCalendar = Calendar.getInstance().apply {
            time = record.time[0]
        }
        val date = Calendar.getInstance().apply {
            time = model.value.selectedDate.toDate()
            set(Calendar.HOUR_OF_DAY, recordCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, recordCalendar.get(Calendar.MINUTE))
        }.time

        navigateToRecordInfo(record, date)
    }

    private fun LocalDate.toDate(): Date {
        return Date.from(
            this
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        )
    }

}