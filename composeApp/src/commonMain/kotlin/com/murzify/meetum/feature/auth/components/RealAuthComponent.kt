package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.feature.auth.components.AuthComponent.Child
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

fun ComponentFactory.createLoginComponent(
    componentContext: ComponentContext,
    navigateToCalendar: () -> Unit,
): AuthComponent = RealAuthComponent(componentContext, navigateToCalendar,this)

class RealAuthComponent(
    componentContext: ComponentContext,
    private val navigateToCalendar: () -> Unit,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, AuthComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Register,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): Child = when (config) {
        is ChildConfig.Register -> Child.Register(
            componentFactory.createRegisterComponent(
                componentContext,
                navigateToSignIn = {
                    navigation.bringToFront(ChildConfig.SignIn)
                },
                navigateToCalendar = navigateToCalendar
            )
        )
        is ChildConfig.SignIn -> Child.SignIn(
            componentFactory.createSignInComponent(
                componentContext,
                navigateToRegister = {
                    navigation.bringToFront(ChildConfig.Register)
                },
                navigateToCalendar
            )
        )
    }

    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object Register : ChildConfig

        @Serializable
        data object SignIn : ChildConfig
    }
}