package com.murzify.meetum.feature.calendar.components

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow

class RealRecordInfoComponent(
    componentContext: ComponentContext,
    val record: Record,
    private val navigateToEdit: (record: Record) -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, RecordInfoComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(record)
    )

    init {
        registerKeeper(Model.serializer()) { model.value }
    }

    override fun onEditClick() {
        navigateToEdit(model.value.record)
    }

    override fun onPhoneLongClick(context: Context) {
        val uri = "tel:${model.value.record}".toUri()
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = uri
        context.startActivity(intent)
    }

    override fun onBackClick() {
        navigateBack()
    }
}