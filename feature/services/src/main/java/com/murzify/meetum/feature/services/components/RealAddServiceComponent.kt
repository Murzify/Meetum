package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.get
import java.util.Currency
import java.util.Locale
import java.util.UUID

fun ComponentFactory.createAddServiceComponent(
    componentContext: ComponentContext,
    service: Service?
) = RealAddServiceComponent(
    componentContext,
    MutableStateFlow(service),
    get(),
    get(),
    get(),
)
class RealAddServiceComponent(
    componentContext: ComponentContext,
    override val service: MutableStateFlow<Service?> = MutableStateFlow(null),
    private val addServicesUseCase: AddServiceUseCase,
    private val recordRepository: RecordRepository,
    private val serviceRepository: ServiceRepository
) : ComponentContext by componentContext, AddServiceComponent {

    override val name: MutableStateFlow<String> = MutableStateFlow(
        service.value?.name ?: ""
    )
    override val price: MutableStateFlow<Double> = MutableStateFlow(
        service.value?.price ?: 0.0
    )
    override val currency: MutableStateFlow<Currency> = MutableStateFlow(
        service.value?.currency ?: Currency.getInstance(Locale.getDefault())
    )
    override val showAlert: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val deleteAlertNeeded: MutableSharedFlow<Boolean> = MutableSharedFlow()
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
        this.name.value = name
    }

    override fun onPriceChanged(price: Double) {
        this.price.value = price
    }

    override fun onCurrencyChanged(currency: Currency) {
        this.currency.value = currency
    }

    override fun onSaveClick() {
        coroutineScope.launch(Dispatchers.IO) {
            if (name.value.isEmpty()) {
                throw AddServiceComponent.Error.Name
            }
            val service = Service(
                name.value,
                price.value,
                currency.value,
                service.value?.id ?: UUID.randomUUID()
            )
            addServicesUseCase(service)
        }
    }

    override fun onDeleteClick() {
        coroutineScope.launch(Dispatchers.IO) {
            if (deleteAlertNeeded.first()) {
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
        }
    }

    override fun onDeleteCanceled() {
        showAlert.value = false
    }


}