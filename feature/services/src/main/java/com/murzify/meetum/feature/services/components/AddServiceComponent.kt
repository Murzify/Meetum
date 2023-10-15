package com.murzify.meetum.feature.services.components

import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Currency

interface AddServiceComponent {
    val service: StateFlow<Service?>
    val name: StateFlow<String>
    val price: MutableStateFlow<Double>
    val currency: StateFlow<Currency>
    val showAlert: StateFlow<Boolean>

    fun onNameChanged(name: String)

    fun onPriceChanged(price: Double)

    fun onCurrencyChanged(currency: Currency)

    fun onSaveClick()

    fun onDeleteClick()

    fun onDeleteConfirmed()

    fun onDeleteCanceled()

    sealed interface Error {
        data object Name : Error, Exception("Name cannot be empty")

    }
}