package com.murzify.meetum.feature.services.components

import com.murzify.meetum.core.domain.model.CurrencySerializer
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.util.Currency

interface AddServiceComponent {
    val model: StateFlow<Model>

    fun onBackClick()

    fun onNameChanged(name: String)

    fun onPriceChanged(price: String)

    fun onCurrencyChanged(currency: Currency?)

    fun onSaveClick()

    fun onDeleteClick()

    fun onDeleteConfirmed()

    fun onDeleteCanceled()

    @Serializable
    data class Model(
        val service: Service?,
        val name: String,
        val isNameError: Boolean,
        val price: String,
        val isPriceError: Boolean,
        @Serializable(with = CurrencySerializer::class)
        val currency: Currency,
        val showAlert: Boolean,
        val showDeleteButton: Boolean,
    )

}