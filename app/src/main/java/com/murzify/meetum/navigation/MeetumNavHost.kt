package com.murzify.meetum.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.murzify.meetum.feature.calendar.navigation.calendarNavigationRoute
import com.murzify.meetum.feature.calendar.navigation.calendarScreen
import com.murzify.meetum.feature.services.navigation.servicesScreen

@Composable
fun MeetumNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = calendarNavigationRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        calendarScreen()
        servicesScreen()
    }
}