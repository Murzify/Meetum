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
import java.util.Date
import javax.inject.Inject

fun ComponentFactory.createRecordsManagerComponent(
    componentContext: ComponentContext,
    splitScreen: Boolean,
    navigateToAddRecord: (date: Date, record: Record?) -> Unit,
): RecordsManagerComponent {
    return RealRecordsManagerComponent(
        componentContext,
        navigateToAddRecord,
        splitScreen,
        get(),
        get(),
        get()
    )
}

class RealRecordsManagerComponent @Inject constructor (
    componentContext: ComponentContext,
    val navigateToAddRecord: (date: Date, record: Record?) -> Unit,
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
    }

    override fun onDateClick(date: LocalDate) {
        selectedDate.value = date
        coroutineScope.launch(Dispatchers.IO) {
            getRecordsUseCase(date.toDate())
        }
    }

    override fun onAddRecordClick() {
        navigateToAddRecord(selectedDate.value.toDate(), null)
    }

    override fun onRecordClick(record: Record) {
        navigateToAddRecord(record.time.first(), record)
    }

    private fun LocalDate.toDate(): Date {
        return Date.from(
            this
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        )
    }

}