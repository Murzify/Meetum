package com.murzify.meetum.feature.services.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.murzify.meetum.feature.services.MeetumServices

const val servicesNavigationRoute = "services"

fun NavGraphBuilder.servicesScreen() {
    composable(route = servicesNavigationRoute) {
        MeetumServices()
    }
}