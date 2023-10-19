package com.murzify.meetum

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.feature.calendar.components.createCalendarComponent
import com.murzify.meetum.feature.services.components.createServicesComponent
import com.murzify.meetum.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
    override val windowSizeClass: WindowSizeClass,
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val shouldShowBottomBar: StateFlow<Boolean> = MutableStateFlow(
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    )
    override val shouldShowNavRail: StateFlow<Boolean> = MutableStateFlow(!shouldShowBottomBar.value)
    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Calendar,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val splitScreen: Boolean = !shouldShowBottomBar.value


    override fun onTabSelected(screen: Screen) {
        val config = when (screen) {
            Screen.Calendar -> ChildConfig.Calendar
            Screen.Services -> ChildConfig.Services(false)
        }
        navigation.bringToFront(config)
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
            componentFactory.createServicesComponent(componentContext, false)
        )
    }

    private sealed interface ChildConfig : Parcelable {

        @Parcelize
        data object Calendar : ChildConfig

        @Parcelize
        data class Services(val addService: Boolean) : ChildConfig
    }
}