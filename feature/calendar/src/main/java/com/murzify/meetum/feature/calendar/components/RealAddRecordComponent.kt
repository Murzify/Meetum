package com.murzify.meetum.feature.calendar.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.util.Calendar
import java.util.Date
import java.util.UUID

fun ComponentFactory.createAddRecordComponent(
    componentContext: ComponentContext,
    date: Date,
    record: Record?,
    navigateBack: () -> Unit,
    navigateToAddService: () -> Unit,
    navigateToRepeat: () -> Unit
) : AddRecordComponent = RealAddRecordComponent(
    componentContext,
    navigateBack,
    navigateToRepeat,
    navigateToAddService,
    MutableStateFlow(date),
    MutableStateFlow(record),
    get(),
    get(),
    get()
)

class RealAddRecordComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
    private val navigateToRepeat: () -> Unit,
    override val onAddServiceClick: () -> Unit,
    override val date: MutableStateFlow<Date>,
    override val record: MutableStateFlow<Record?> = MutableStateFlow(null),
    private val addRecordUseCase: AddRecordUseCase,
    private val getServicesUseCase: GetServicesUseCase,
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
    override val isServiceError = MutableStateFlow(false)
    override val services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())
    override val repeat = MutableStateFlow<Repeat>(
        RepeatRecord.Repeater()
            .end(1)
            .repeat()
    )
    override val showRepeatInfo = MutableStateFlow(false)

    private val coroutineScope = componentCoroutineScope()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect {
                    services.value = it
                }
        }
    }

    override fun onTimeChanged(hours: Int, minutes: Int) {
        Calendar.getInstance().apply {
            time = date.value
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            date.value = time
        }
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

    override fun onContactsClicked(uri: Uri, contentResolver: ContentResolver) {
        val contactFields = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts._ID,
        )
        val cursor = contentResolver.query(
            uri,
            contactFields,
            null,
            null,
            null
        ) ?: return

        cursor.use { cur ->
            if (cur.count == 0) return
            cur.moveToFirst()
            val name = cur.getString(0)
            this.name.value = name
            val contactId = cur.getString(1)
            val phonesFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val phones = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phonesFields,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null, null
            ) ?: return
            phones.use { ph ->
                ph.moveToFirst()
                val phone = ph.getString(0)
                this.phone.value = phone
            }
        }
    }

    override fun onRepeatClicked() {
        navigateToRepeat()
    }

    override fun onServiceSelected(service: Service) {
        isServiceError.value = false
        this.service.value = service
    }

    override fun onSaveClicked() {
        if (service.value == null) {
            isServiceError.value = true
            return
        }
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
                    saveRecord,
                    repeat.value
                )
            } else {
                recordRepo.updateRecord(saveRecord)
            }
            navigateBack()
        }
    }

    override fun onDeleteClicked() {
        coroutineScope.launch(Dispatchers.IO) {
            recordRepo.deleteRecord(record.value!!)
            navigateBack()
        }
    }

    override fun onBackClick() {
        navigateBack()
    }

    override fun onRepeatReceived(repeat: Repeat) {
        showRepeatInfo.value = true
        this.repeat.value = repeat
    }
}