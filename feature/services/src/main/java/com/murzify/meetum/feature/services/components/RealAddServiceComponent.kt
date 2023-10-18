package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.util.Currency
import java.util.Locale
import java.util.UUID

fun ComponentFactory.createAddServiceComponent(
    componentContext: ComponentContext,
    service: Service?,
    navigateBack: () -> Unit
) = RealAddServiceComponent(
    componentContext,
    MutableStateFlow(service),
    navigateBack,
    get(),
    get(),
    get(),
)
class RealAddServiceComponent(
    componentContext: ComponentContext,
    override val service: MutableStateFlow<Service?> = MutableStateFlow(null),
    private val navigateBack: () -> Unit,
    private val addServicesUseCase: AddServiceUseCase,
    private val recordRepository: RecordRepository,
    private val serviceRepository: ServiceRepository
) : ComponentContext by componentContext, AddServiceComponent {

    override val name: MutableStateFlow<String> = MutableStateFlow(
        service.value?.name ?: ""
    )
    override val isNameError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val price: MutableStateFlow<Double> = MutableStateFlow(
        service.value?.price ?: 0.0
    )
    override val isPriceError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val currency: MutableStateFlow<Currency> = MutableStateFlow(
        service.value?.currency ?: Currency.getInstance(Locale.getDefault())
    )

    override val showAlert: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val showDeleteButton: StateFlow<Boolean> = MutableStateFlow(service.value != null)
    override fun onBackClick() {
        navigateBack()
    }

    private val deleteAlertNeeded = MutableStateFlow(false)

    private val coroutineScope = componentCoroutineScope()
    init {
        coroutineScope.launch(Dispatchers.IO) {
            service.value?.let {
                recordRepository.futureRecords(it.id).apply {
                    if (isNotEmpty()) {
                        deleteAlertNeeded.emit(true)
                    } else {
                        deleteAlertNeeded.emit(false)
                    }
                }
            }
        }
    }

    override fun onNameChanged(name: String) {
        this.name.value = name.removePrefix(" ")
    }

    override fun onPriceChanged(price: String) {
        isPriceError.value = false
        try {
            this.price.value = price
                .replace(",", ".")
                .replace("-", "")
                .toDouble()
        } catch (e: Throwable) {
            isPriceError.value = true
        }
    }

    override fun onCurrencyChanged(currency: Currency?) {
        currency?.let {
            this.currency.value = it
        }
    }

    override fun onSaveClick() {
        coroutineScope.launch(Dispatchers.IO) {
            isNameError.value = name.value.isEmpty()
            if (!isNameError.value) {
                val service = Service(
                    name.value,
                    price.value,
                    currency.value,
                    service.value?.id ?: UUID.randomUUID()
                )
                addServicesUseCase(service)
                navigateBack()
            }

        }
    }

    override fun onDeleteClick() {
        coroutineScope.launch(Dispatchers.IO) {
            if (deleteAlertNeeded.value) {
                showAlert.value = true
            } else {
                onDeleteConfirmed()
            }
        }

    }

    override fun onDeleteConfirmed() {
        coroutineScope.launch(Dispatchers.IO) {
            showAlert.value = false
            service.value?.let {
                recordRepository.deleteLinkedRecords(it.id)
                serviceRepository.deleteService(it)
            }
            navigateBack()
        }
    }

    override fun onDeleteCanceled() {
        showAlert.value = false
    }

}