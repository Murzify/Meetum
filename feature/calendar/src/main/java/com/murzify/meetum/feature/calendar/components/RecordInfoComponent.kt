package com.murzify.meetum.feature.calendar.components

import android.content.Context
import com.murzify.meetum.core.domain.model.DateSerializer
import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Date

interface RecordInfoComponent {

    val model: StateFlow<Model>

    fun onEditClick()

    fun onPhoneLongClick(context: Context)

    fun onBackClick()

    @Serializable
    data class Model(
        val record: Record,
        @Serializable(with = DateSerializer::class)
        val date: Date
    )
}