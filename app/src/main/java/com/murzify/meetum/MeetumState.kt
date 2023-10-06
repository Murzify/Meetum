package com.murzify.meetum

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.murzify.meetum.feature.calendar.CalendarState
import com.murzify.meetum.feature.calendar.navigation.calendarNavigationRoute
import com.murzify.meetum.feature.services.navigation.servicesNavigationRoute
import com.murzify.meetum.navigation.Screen


@Composable
fun rememberMeetumState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
): AppState {
    return remember(navController, windowSizeClass) {
        MeetumState(navController, windowSizeClass)
    }
}

interface AppState: CalendarState {

    val navController: NavHostController

    val windowSizeClass: WindowSizeClass
    val currentDestination: @Composable NavDestination?
         @Composable get

    val currentScreen: Screen?
        @Composable get

    val shouldShowBottomBar: Boolean

    val shouldShowNavRail: Boolean

    val screensList: List<Screen>
}

class MeetumState(
    override val navController: NavHostController,
    override val windowSizeClass: WindowSizeClass,
): AppState {
    override val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination
    override val currentScreen: Screen?
        @Composable get() = when (currentDestination?.route) {
            calendarNavigationRoute -> Screen.Calendar
            servicesNavigationRoute -> Screen.Services
            else -> null
        }
    override val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    override val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    override val screensList: List<Screen>
        get() = listOf(Screen.Calendar, Screen.Services)

    override val shouldSplitCalendarScreen: Boolean
        get() = !shouldShowBottomBar

}