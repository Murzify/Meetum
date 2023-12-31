package com.murzify.meetum.feature.calendar.components

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface AddRecordComponent {
    val model: StateFlow<Model>
    val onAddServiceClick: () -> Unit

    fun onTimeChanged(hours: Int, minutes: Int)

    fun onNameChanged(name: String)

    fun onDescriptionChanged(description: String)

    fun onPhoneChanged(phone: String)

    fun onContactsClicked(name: String, phone: String)

    fun onRepeatClicked()

    fun onServiceSelected(service: Service)

    fun onSaveClicked()

    fun onDeleteClicked()

    fun onAlertDeleteTypeSelected(deleteType: DeleteType)

    fun onDeleteCancel()

    fun onBackClick()

    fun onRepeatReceived(repeat: Repeat)

    @Serializable
    data class Model(
        val date: Instant,
        val name: String,
        val description: String,
        val phone: String,
        val service: Service?,
        val isServiceError: Boolean,
        val record: Record?,
        val services: List<Service>,
        val repeat: Repeat,
        val showRepeatInfo: Boolean,
        val showSeriesAlert: Boolean
    )

    sealed interface DeleteType {
        data object Series: DeleteType

        data object Date: DeleteType
    }
}