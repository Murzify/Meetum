package com.murzify.meetum.feature.calendar.components

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow

fun ComponentFactory.createRecordInfoComponent(
    componentContext: ComponentContext,
    record: StateFlow<Record>,
    navigateToEdit: (record: Record) -> Unit,
    navigateBack: () -> Unit
): RecordInfoComponent = RealRecordInfoComponent(
    componentContext,
    record,
    navigateToEdit,
    navigateBack
)

class RealRecordInfoComponent(
    componentContext: ComponentContext,
    override val record: StateFlow<Record>,
    private val navigateToEdit: (record: Record) -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, RecordInfoComponent {

    override fun onEditClick() {
        navigateToEdit(record.value)
    }

    override fun onPhoneLongClick(context: Context) {
        val uri = "tel:${record.value.phone}".toUri()
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = uri
        context.startActivity(intent)
    }

    override fun onBackClick() {
        navigateBack()
    }
}