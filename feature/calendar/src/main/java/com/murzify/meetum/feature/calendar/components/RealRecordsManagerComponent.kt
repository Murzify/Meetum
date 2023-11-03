package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
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
    navigateToRecordInfo: (record: Record) -> Unit,
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
    private val navigateToRecordInfo: (record: Record) -> Unit,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepository: RecordRepository,
    private val getRecordsUseCase: GetRecordsUseCase,
): ComponentContext by componentContext, RecordsManagerComponent {

    companion object {
        const val STATE_KEY = "STATE"
    }

    override val model = MutableStateFlow(
        test() ?: Model(
            currentRecords = emptyList(),
            services = emptyList(),
            allRecords = emptyList(),
            selectedDate = LocalDate.now()
        )
    )

    fun test(): Model? {
        val r = stateKeeper.consume(key = STATE_KEY, strategy = Model.serializer())
        return r
    }

    private val coroutineScope = componentCoroutineScope()

    init {
        stateKeeper.register(key = STATE_KEY, strategy = Model.serializer()) { model.value }

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
        navigateToAddRecord(model.value.selectedDate.toDate(), null)
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