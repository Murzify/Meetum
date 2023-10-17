package com.murzify.meetum.feature.calendar.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.util.Date
import java.util.UUID

fun ComponentFactory.createAddRecordComponent(
    componentContext: ComponentContext,
    date: Date,
    record: Record?,
    navigateBack: () -> Unit,
    navigateToAddService: () -> Unit
) : AddRecordComponent = RealAddRecordComponent(
    componentContext,
    navigateBack,
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
    override val services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())

    private val coroutineScope = componentCoroutineScope()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect {
                    services.value = it
                }
        }
    }

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
        TODO("Repeat records")
    }

    override fun onServiceSelected(service: Service) {
        this.service.value = service
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
            navigateBack()
        }
    }

    override fun onBackClick() {
        navigateBack()
    }
}