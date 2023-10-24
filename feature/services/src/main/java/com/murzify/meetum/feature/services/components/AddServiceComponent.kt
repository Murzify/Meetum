package com.murzify.meetum.feature.services.components

import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import java.util.Currency

interface AddServiceComponent {
    val service: StateFlow<Service?>
    val name: StateFlow<String>
    val isNameError: StateFlow<Boolean>
    val price: StateFlow<String>
    val isPriceError: StateFlow<Boolean>
    val currency: StateFlow<Currency>
    val showAlert: StateFlow<Boolean>
    val showDeleteButton: StateFlow<Boolean>

    fun onBackClick()

    fun onNameChanged(name: String)

    fun onPriceChanged(price: String)

    fun onCurrencyChanged(currency: Currency?)

    fun onSaveClick()

    fun onDeleteClick()

    fun onDeleteConfirmed()

    fun onDeleteCanceled()

}