package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow

fun ComponentFactory.createRecordInfoComponent(
    componentContext: ComponentContext,
    record: StateFlow<Record>,
    navigateToEdit: (record: Record) -> Unit
): RecordInfoComponent = RealRecordInfoComponent(
    componentContext,
    record,
    navigateToEdit
)

class RealRecordInfoComponent(
    componentContext: ComponentContext,
    override val record: StateFlow<Record>,
    private val navigateToEdit: (record: Record) -> Unit
) : ComponentContext by componentContext, RecordInfoComponent {

    override fun onEditClick() {
        navigateToEdit(record.value)
    }

    override fun onPhoneClick() {
        TODO("Not yet implemented")
    }
}