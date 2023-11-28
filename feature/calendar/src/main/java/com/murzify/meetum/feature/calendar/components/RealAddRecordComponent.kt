package com.murzify.meetum.feature.calendar.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.DeleteType
import com.murzify.meetum.feature.calendar.components.AddRecordComponent.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.get
import java.util.UUID

fun ComponentFactory.createAddRecordComponent(
    componentContext: ComponentContext,
    date: Instant,
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
    date,
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
    date: Instant,
    record: Record? = null,
    private val addRecordUseCase: AddRecordUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepo: RecordRepository
) :  ComponentContext by componentContext, AddRecordComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            date = date,
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
        coroutineScope.launch(Dispatchers.IO) {
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
            model.update { it.copy(name = name) }
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
                model.update { it.copy(phone = phone) }
            }
        }
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
            val saveRecord = Record(
                clientName = name.takeIf { it.isNotEmpty() },
                time = listOf(date),
                description = description.takeIf { it.isNotEmpty() },
                phone = phone.takeIf { it.isNotEmpty() },
                service = service!!,
                id = record?.id ?: UUID.randomUUID()
            )

            coroutineScope.launch(Dispatchers.IO) {
                if (record == null) {
                    addRecordUseCase(
                        saveRecord,
                        repeat
                    )
                } else {
                    recordRepo.updateRecord(saveRecord)
                }
                withContext(Dispatchers.Main) {
                    navigateToCalendar()
                }
            }
        }

    }

    override fun onDeleteClicked() {
        coroutineScope.launch(Dispatchers.IO) {
            val record = model.value.record!!
            if (record.time.size > 1) {
                model.update { it.copy(showSeriesAlert = true) }
                return@launch
            }
            recordRepo.deleteRecord(record)
            withContext(Dispatchers.Main) {
                navigateToCalendar()
            }
        }
    }

    override fun onAlertDeleteTypeSelected(deleteType: DeleteType) {
        coroutineScope.launch(Dispatchers.IO) {
            val record = model.value.record!!
            when (deleteType) {
                DeleteType.Date -> recordRepo.deleteDate(record.id, model.value.date)
                DeleteType.Series -> recordRepo.deleteRecord(record)
            }
            model.update { it.copy(showSeriesAlert = false) }
            withContext(Dispatchers.Main) {
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