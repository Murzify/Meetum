package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get

fun ComponentFactory.createServicesListComponent(
    componentContext: ComponentContext,
    navigateToAddService: (Service?) -> Unit,
) = RealServicesListComponent(
    componentContext,
    navigateToAddService,
    get()
)

class RealServicesListComponent(
    componentContext: ComponentContext,
    private val navigateToAddService: (Service?) -> Unit,
    private val getServicesUseCase: GetServicesUseCase,
) : ComponentContext by componentContext, ServicesListComponent {
    override val services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())
    override val showGhostLottie: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val coroutineScope = componentCoroutineScope()

    init {
        coroutineScope.launch(meetumDispatchers.io) {
            getServicesUseCase()
                .collect {
                    showGhostLottie.value = it.isEmpty()
                    services.value = it
                }
        }
    }

    override fun onServiceClick(service: Service) {
        // edit service
        navigateToAddService(service)
    }

    override fun onAddServiceClick() {
        navigateToAddService(null)
    }

}