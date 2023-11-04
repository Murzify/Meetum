package com.murzify.meetum.feature.calendar.components

import android.content.ContentResolver
import android.net.Uri
import com.murzify.meetum.core.domain.model.DateSerializer
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Date

interface AddRecordComponent {
    val model: StateFlow<Model>
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

    @Serializable
    data class Model(
        @Serializable(with = DateSerializer::class)
        val date: Date,
        val name: String,
        val description: String,
        val phone: String,
        val service: Service?,
        val isServiceError: Boolean,
        val record: Record?,
        val services: List<Service>,
        val repeat: Repeat,
        val showRepeatInfo: Boolean
    )
}