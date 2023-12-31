package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant

class RealRecordInfoComponent(
    componentContext: ComponentContext,
    record: Record,
    date: Instant,
    private val navigateToEdit: (record: Record) -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, RecordInfoComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(record, date)
    )

    init {
        registerKeeper(Model.serializer()) { model.value }
    }

    override fun onEditClick() {
        navigateToEdit(model.value.record)
    }

    override fun onBackClick() {
        navigateBack()
    }
}