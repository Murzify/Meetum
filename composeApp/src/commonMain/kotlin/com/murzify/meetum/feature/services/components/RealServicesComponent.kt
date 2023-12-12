package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

fun ComponentFactory.createServicesComponent(
    componentContext: ComponentContext,
    addService: Boolean,
    navigateBack: () -> Unit
): ServicesComponent = RealServicesComponent(
    componentContext,
    this,
    addService,
    navigateBack
)

class RealServicesComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
    private val addService: Boolean,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, ServicesComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, ServicesComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = if (addService)
            ChildConfig.AddService(null) else ChildConfig.ServicesList,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): ServicesComponent.Child = when (config) {
        is ChildConfig.AddService -> ServicesComponent.Child.AddService(
            componentFactory.createAddServiceComponent(
                componentContext,
                config.service,
                navigateBack = {
                    if (addService) {
                        navigateBack()
                    } else {
                        navigation.pop()
                    }
                }
            )
        )
        ChildConfig.ServicesList -> ServicesComponent.Child.ServicesList(
            componentFactory.createServicesListComponent(componentContext) {
                navigation.push(ChildConfig.AddService(it))
            }
        )
    }

    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object ServicesList: ChildConfig

        @Serializable
        data class AddService(val service: Service?): ChildConfig
    }
}