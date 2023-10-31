package com.murzify.meetum

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.arkivanov.decompose.router.stack.ChildStack
import com.murzify.meetum.feature.calendar.components.CalendarComponent
import com.murzify.meetum.feature.services.components.ServicesComponent
import com.murzify.meetum.navigation.Screen
import kotlinx.coroutines.flow.StateFlow

interface RootComponent {
    val splitScreen: Boolean
    val shouldShowBottomBar: StateFlow<Boolean>
    val shouldShowNavRail: StateFlow<Boolean>

    val childStack: StateFlow<ChildStack<*, Child>>

    fun onTabSelected(screen: Screen)

    fun onCalcWindow(windowSizeClass: WindowSizeClass)

    sealed interface Child {
        data class Calendar(val component: CalendarComponent): Child

        data class Services(val component: ServicesComponent): Child
    }
}
