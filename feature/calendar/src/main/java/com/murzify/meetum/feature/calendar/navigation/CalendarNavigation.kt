package com.murzify.meetum.feature.calendar.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.murzify.meetum.feature.calendar.AddRecordRoute
import com.murzify.meetum.feature.calendar.CalendarViewModel
import com.murzify.meetum.feature.calendar.MeetumCalendarRoute
import java.util.Date

const val calendarNavigationRoute = "calendar"
const val mainCalendarNavigationRoute = "mainCalendar"
const val addRecordNavigationRoute = "addRecord?editing={editing}&date={date}"

fun NavGraphBuilder.calendarScreen(
    navController: NavController,
    navigateToAddService: () -> Unit
) {
    navigation(route = calendarNavigationRoute, startDestination = mainCalendarNavigationRoute) {
        composable(route = mainCalendarNavigationRoute) {
            MeetumCalendarRoute(
                navigateToAddRecord = { isEditing, date ->
                    navController.navigate("addRecord?editing=$isEditing&date=${date.time}")
                }
            )
        }

        composable(
            route = addRecordNavigationRoute,
            arguments = listOf(
                navArgument("editing") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("date") {
                    type = NavType.LongType
                    defaultValue = 0
                }
            )
        ) { navBackStackEntry ->
            val mainCalendarEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(mainCalendarNavigationRoute)
            }
            val calendarViewModel = hiltViewModel<CalendarViewModel>(mainCalendarEntry)

            val time = navBackStackEntry.arguments?.getLong("date") ?: 0
            AddRecordRoute(
                isEditing = navBackStackEntry.arguments?.getBoolean("editing") ?: false,
                date = Date(time),
                viewModel = calendarViewModel,
                navigateToBack = {
                    navController.popBackStack()
                },
                navigateToAddService = navigateToAddService
            )
        }
    }

}