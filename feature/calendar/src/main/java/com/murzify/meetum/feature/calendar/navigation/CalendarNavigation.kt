package com.murzify.meetum.feature.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.murzify.meetum.feature.calendar.MeetumCalendar

const val calendarNavigationRoute = "calendar"

fun NavGraphBuilder.calendarScreen() {
    composable(route = calendarNavigationRoute) {
        MeetumCalendar()
    }
}