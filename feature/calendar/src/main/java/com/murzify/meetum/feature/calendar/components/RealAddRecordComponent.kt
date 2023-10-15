package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.util.Date
import java.util.UUID

fun ComponentFactory.createAddRecordComponent(
    componentContext: ComponentContext,
    date: Date,
    record: Record?
) : AddRecordComponent = RealAddRecordComponent(
    componentContext,
    MutableStateFlow(date),
    MutableStateFlow(record),
    get(),
    get()
)

class RealAddRecordComponent(
    componentContext: ComponentContext,
    override val date: MutableStateFlow<Date>,
    override val record: MutableStateFlow<Record?> = MutableStateFlow(null),
    private val addRecordUseCase: AddRecordUseCase,
    private val recordRepo: RecordRepository
) :  ComponentContext by componentContext, AddRecordComponent {

    override val name: MutableStateFlow<String> = MutableStateFlow(
        record.value?.clientName ?: ""
    )
    override val description: MutableStateFlow<String> = MutableStateFlow(
        record.value?.description ?: ""
    )
    override val phone: MutableStateFlow<String> = MutableStateFlow(
        record.value?.phone ?: ""
    )
    override val service: MutableStateFlow<Service?> = MutableStateFlow(
        record.value?.service
    )

    private val coroutineScope = componentCoroutineScope()

    override fun onTimeChanged(time: Date) {
        date.value = time
    }

    override fun onNameChanged(name: String) {
        this.name.value = name
    }

    override fun onDescriptionChanged(description: String) {
        this.description.value = description
    }

    override fun onPhoneChanged(phone: String) {
        this.phone.value = phone
    }

    override fun onContactsClicked() {
        TODO("Import client from contacts")
    }

    override fun onRepeatClicked() {
        TODO("Repeat records")
    }

    override fun onSaveClicked() {
        if (service.value == null) throw IllegalStateException("")
        val saveRecord = Record(
            clientName = name.value.takeIf { it.isNotEmpty() },
            time = listOf(date.value),
            description = description.value.takeIf { it.isNotEmpty() },
            phone = phone.value.takeIf { it.isNotEmpty() },
            service = service.value!!,
            id = record.value?.id ?: UUID.randomUUID()
        )
        coroutineScope.launch(Dispatchers.IO) {
            if (record.value == null) {
                addRecordUseCase(
                    saveRecord
                )
            } else {
                recordRepo.updateRecord(saveRecord)
            }
        }
    }

    override fun onDeleteClicked() {
        coroutineScope.launch(Dispatchers.IO) {
            recordRepo.deleteRecord(record.value!!)
        }
    }
}