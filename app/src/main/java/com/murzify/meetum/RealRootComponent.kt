package com.murzify.meetum

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.feature.calendar.components.createCalendarComponent
import com.murzify.meetum.feature.services.components.createServicesComponent
import com.murzify.meetum.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val shouldShowBottomBar= MutableStateFlow(true)

    override val shouldShowNavRail = MutableStateFlow(false)

    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Calendar,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val splitScreen: Boolean get() = !shouldShowBottomBar.value

    override fun onTabSelected(screen: Screen) {
        val config = when (screen) {
            Screen.Calendar -> ChildConfig.Calendar
            Screen.Services -> ChildConfig.Services(false)
        }
        navigation.bringToFront(config)
    }

    override fun onCalcWindow(windowSizeClass: WindowSizeClass) {
        shouldShowBottomBar.value = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
        shouldShowNavRail.value = !shouldShowBottomBar.value
    }

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        ChildConfig.Calendar -> RootComponent.Child.Calendar(
            componentFactory.createCalendarComponent(componentContext, splitScreen) {
                navigation.push(ChildConfig.Services(true))
            }
        )
        is ChildConfig.Services -> RootComponent.Child.Services(
            componentFactory.createServicesComponent(
                componentContext,
                config.addService,
                navigateBack = navigation::pop
            )

        )
    }

    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object Calendar : ChildConfig

        @Serializable
        data class Services(val addService: Boolean) : ChildConfig
    }
}