package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun ComponentFactory.createRecordsManagerComponent(
    componentContext: ComponentContext,
    splitScreen: Boolean,
    navigateToAddRecord: (date: Date, record: Record?) -> Unit,
    navigateToRecordInfo: (record: Record) -> Unit,
): RecordsManagerComponent {
    return RealRecordsManagerComponent(
        componentContext,
        navigateToAddRecord,
        navigateToRecordInfo,
        splitScreen,
        get(),
        get(),
        get()
    )
}

class RealRecordsManagerComponent constructor (
    componentContext: ComponentContext,
    private val navigateToAddRecord: (date: Date, record: Record?) -> Unit,
    private val navigateToRecordInfo: (record: Record) -> Unit,
    override val splitScreen: Boolean,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepository: RecordRepository,
    private val getRecordsUseCase: GetRecordsUseCase,
): ComponentContext by componentContext, RecordsManagerComponent {
    override val currentRecords: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())

    override val services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())

    override val allRecords: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())

    override val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())

    private val coroutineScope = componentCoroutineScope()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect {
                    services.value = it
                }
        }

        coroutineScope.launch(Dispatchers.IO) {
            recordRepository.getAllRecords().collect {
                allRecords.value = it
            }
        }

        coroutineScope.launch(Dispatchers.IO) {
            getRecordsUseCase(selectedDate.value.toDate()).collect {
                currentRecords.value = it
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
        selectedDate.value = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        coroutineScope.launch(Dispatchers.IO) {
            getRecordsUseCase(date.toDate()).collect {
                currentRecords.value = it
            }
        }
    }

    override fun onAddRecordClick() {
        navigateToAddRecord(selectedDate.value.toDate(), null)
    }

    override fun onRecordClick(record: Record) {
        navigateToRecordInfo(record)
    }

    private fun LocalDate.toDate(): Date {
        return Date.from(
            this
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        )
    }

}