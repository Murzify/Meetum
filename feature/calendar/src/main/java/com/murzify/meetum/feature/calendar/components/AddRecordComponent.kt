package com.murzify.meetum.feature.calendar.components

import android.content.ContentResolver
import android.net.Uri
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface AddRecordComponent {
    val date: StateFlow<Date>
    val name: StateFlow<String>
    val description: StateFlow<String>
    val phone: StateFlow<String>
    val service: StateFlow<Service?>
    val isServiceError: StateFlow<Boolean>
    val record: StateFlow<Record?>
    val services: StateFlow<List<Service>>
    val repeat: StateFlow<Repeat>
    val onAddServiceClick: () -> Unit

    fun onTimeChanged(hours: Int, minutes: Int)

    fun onNameChanged(name: String)

    fun onDescriptionChanged(description: String)

    fun onPhoneChanged(phone: String)

    fun onContactsClicked(uri: Uri, contentResolver: ContentResolver)

    fun onRepeatClicked()

    fun onServiceSelected(service: Service)

    fun onSaveClicked()

    fun onDeleteClicked()

    fun onBackClick()

    fun onRepeatReceived(repeat: Repeat)

}