package com.murzify.meetum.feature.calendar.components

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface AddRecordComponent {
    val date: StateFlow<Date>
    val name: StateFlow<String>
    val description: StateFlow<String>
    val phone: StateFlow<String>
    val service: StateFlow<Service?>
    val record: StateFlow<Record?>

    fun onTimeChanged(time: Date)

    fun onNameChanged(name: String)

    fun onDescriptionChanged(description: String)

    fun onPhoneChanged(phone: String)

    fun onContactsClicked()

    fun onRepeatClicked()

    fun onSaveClicked()

    fun onDeleteClicked()

}