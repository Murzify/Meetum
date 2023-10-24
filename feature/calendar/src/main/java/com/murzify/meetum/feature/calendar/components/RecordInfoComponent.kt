package com.murzify.meetum.feature.calendar.components

import android.content.Context
import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow

interface RecordInfoComponent {

    val record: StateFlow<Record>

    fun onEditClick()

    fun onPhoneLongClick(context: Context)

    fun onBackClick()
}