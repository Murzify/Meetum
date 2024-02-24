package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.DeleteType
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.Model
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.get

fun ComponentFactory.createAddRecordComponent(
    componentContext: ComponentContext,
    recordTime: RecordTime,
    record: Record?,
    navigateBack: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAddService: () -> Unit,
    navigateToRepeat: () -> Unit
) : AddRecordComponent = RealAddRecordComponent(
    componentContext,
    navigateBack,
    navigateToCalendar,
    navigateToRepeat,
    navigateToAddService,
    recordTime,
    record,
    get(),
    get(),
    get()
)

class RealAddRecordComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
    private val navigateToCalendar: () -> Unit,
    private val navigateToRepeat: () -> Unit,
    override val onAddServiceClick: () -> Unit,
    private val recordTime: RecordTime,
    record: Record? = null, // if the record is not null, then record in edit mode
    private val addRecordUseCase: AddRecordUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepo: RecordRepository
) :  ComponentContext by componentContext, AddRecordComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            date = recordTime.time,
            name = record?.clientName ?: "",
            description = record?.description ?: "",
            phone =record?.phone ?: "",
            service = record?.service,
            isServiceError = false,
            record = record,
            services = emptyList(),
            repeat = RepeatRecord.Repeater()
                .end(1)
                .repeat(),
            showRepeatInfo = false,
            showSeriesAlert = false
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
    }

    override fun onTimeChanged(hours: Int, minutes: Int) {
        val tz = TimeZone.currentSystemDefault()
        val currentDateTime = model.value.date.toLocalDateTime(tz)
        val localDate = currentDateTime.date
        val localTime = LocalTime(hours, minutes, 0, 0)
        val newInstant = LocalDateTime(localDate, localTime).toInstant(tz)
        model.update { it.copy(date = newInstant) }
    }

    override fun onNameChanged(name: String) {
        model.update { it.copy(name = name) }
    }

    override fun onDescriptionChanged(description: String) {
        model.update { it.copy(description = description) }
    }

    override fun onPhoneChanged(phone: String) {
        model.update { it.copy(phone = phone) }
    }

    override fun onContactsClicked(name: String, phone: String) {
        model.update { it.copy(name = name) }
        model.update { it.copy(phone = phone) }
    }

    override fun onRepeatClicked() {
        navigateToRepeat()
    }

    override fun onServiceSelected(service: Service) {
        model.update { it.copy(isServiceError = false) }
        model.update { it.copy(service = service) }
    }

    override fun onSaveClicked() {
        if (model.value.service == null) {
            model.update { it.copy(isServiceError = true) }
            return
        }
        model.value.apply {
            val dates = if (record == null) {
                mutableListOf(
                    recordTime.copy(time = date)
                )
            } else {
                val updateIndex = record.dates.indexOfFirst { it.id == recordTime.id }
                val newRecordTime = record.dates[updateIndex].copy(time = date)
                record.dates.toMutableList().apply {
                    this[updateIndex] = newRecordTime
                }
            }
            val saveRecord = Record(
                clientName = name.takeIf { it.isNotEmpty() },
                dates = dates,
                description = description.takeIf { it.isNotEmpty() },
                phone = phone.takeIf { it.isNotEmpty() },
                service = service!!,
                id = record?.id ?: Uuid.randomUUID()
            )

            coroutineScope.launch(meetumDispatchers.io) {
                if (record == null) {
                    addRecordUseCase(
                        saveRecord,
                        repeat
                    )
                } else {
                    recordRepo.updateRecord(saveRecord)
                }
                withContext(meetumDispatchers.main) {
                    navigateToCalendar()
                }
            }
        }

    }

    override fun onDeleteClicked() {
        coroutineScope.launch(meetumDispatchers.io) {
            val record = model.value.record!!
            if (record.dates.size > 1) {
                model.update { it.copy(showSeriesAlert = true) }
                return@launch
            }
            recordRepo.deleteRecord(record)
            withContext(meetumDispatchers.main) {
                navigateToCalendar()
            }
        }
    }

    override fun onAlertDeleteTypeSelected(deleteType: DeleteType) {
        coroutineScope.launch(meetumDispatchers.io) {
            val record = model.value.record!!
            when (deleteType) {
                DeleteType.Date -> recordRepo.deleteDate(recordTime, record.id)
                DeleteType.Series -> recordRepo.deleteRecord(record)
            }
            model.update { it.copy(showSeriesAlert = false) }
            withContext(meetumDispatchers.main) {
                navigateToCalendar()
            }
        }
    }

    override fun onDeleteCancel() {
        model.update { it.copy(showSeriesAlert = false) }
    }

    override fun onBackClick() {
        navigateBack()
    }

    override fun onRepeatReceived(repeat: Repeat) {
        model.update { it.copy(showRepeatInfo = true) }
        model.update { it.copy(repeat = repeat) }
    }
}