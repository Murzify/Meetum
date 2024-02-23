package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.ComponentContext
import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import com.murzify.meetum.feature.services.components.AddServiceComponent.Model
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get
import java.util.Currency
import java.util.Locale

fun ComponentFactory.createAddServiceComponent(
    componentContext: ComponentContext,
    service: Service?,
    navigateBack: () -> Unit
) = RealAddServiceComponent(
    componentContext,
    service,
    navigateBack,
    get(),
    get(),
    get(),
)
class RealAddServiceComponent(
    componentContext: ComponentContext,
    service: Service? = null,
    private val navigateBack: () -> Unit,
    private val addServicesUseCase: AddServiceUseCase,
    private val recordRepository: RecordRepository,
    private val serviceRepository: ServiceRepository
) : ComponentContext by componentContext, AddServiceComponent {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            service = service,
            name = service?.name ?: "",
            isNameError = false,
            price = service?.price?.toString() ?: "",
            isPriceError = false,
            currency = service?.currency ?: Currency.getInstance(Locale.getDefault()),
            showAlert = false,
            showDeleteButton = service != null
        )
    )

    init {
        registerKeeper(Model.serializer()) { model.value }
    }

    override fun onBackClick() {
        navigateBack()
    }

    private val deleteAlertNeeded = MutableStateFlow(false)

    private val coroutineScope = componentCoroutineScope()
    init {
        coroutineScope.launch(meetumDispatchers.io) {
            model.value.service?.let {
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
        model.update { it.copy(name = name.removePrefix(" ")) }
    }

    override fun onPriceChanged(price: String) {
        model.update { it.copy(isPriceError = false) }
        try {
            price
                .replace(",", ".")
                .replace("-", "")
                .toDouble()
                .takeIf {
                    it != Double.POSITIVE_INFINITY && it != Double.NEGATIVE_INFINITY
                }?.let { formattedPrice ->
                    model.update { model ->
                        model.copy(
                            price = formattedPrice.toString()
                        )
                    }
                }
        } catch (e: Throwable) {
            model.update { it.copy(isPriceError = true) }
        }
    }

    override fun onCurrencyChanged(currency: Currency?) {
        currency?.let { cur ->
            model.update { it.copy(currency = cur) }
        }
    }

    override fun onSaveClick() {
        coroutineScope.launch(meetumDispatchers.io) {
            model.updateAndGet {
                it.copy(isNameError = model.value.name.isEmpty())
            }.apply {
                if (isNameError) {
                    return@apply
                } else if (price.isEmpty()) {
                    model.update { it.copy(isPriceError = true) }
                    return@apply
                }
                val service = Service(
                    name,
                    price.toDouble(),
                    currency,
                    service?.id ?: Uuid.randomUUID()
                )
                if (showDeleteButton) {
                    serviceRepository.editService(service)
                } else {
                    addServicesUseCase(service)
                }

                withContext(meetumDispatchers.main) {
                    navigateBack()
                }
            }


        }
    }

    override fun onDeleteClick() {
        coroutineScope.launch(meetumDispatchers.io) {
            if (deleteAlertNeeded.value) {
                model.update { it.copy(showAlert = true) }
            } else {
                onDeleteConfirmed()
            }
        }

    }

    override fun onDeleteConfirmed() {
        coroutineScope.launch(meetumDispatchers.io) {
            model.update { it.copy(showAlert = false) }
            model.value.service?.let {
                recordRepository.deleteLinkedRecords(it.id)
                serviceRepository.deleteService(it)
            }
            withContext(meetumDispatchers.main){
                navigateBack()
            }
        }
    }

    override fun onDeleteCanceled() {
        model.update { it.copy(showAlert = false) }
    }

}