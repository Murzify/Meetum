package com.murzify.meetum.feature.services.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.murzify.meetum.feature.services.AddServiceRoute
import com.murzify.meetum.feature.services.ServicesListRoute

const val servicesNavigationRoute = "services"
const val servicesListNavigationRoute = "servicesList"
const val addServiceNavigationRoute = "addService"

fun NavGraphBuilder.servicesScreen(navController: NavHostController) {
    navigation(route = servicesNavigationRoute, startDestination = servicesListNavigationRoute) {
        composable(route = servicesListNavigationRoute) {
            ServicesListRoute(
                navigateToAddService = {
                    navController.navigate(addServiceNavigationRoute)
                }
            )
        }
        composable(route = addServiceNavigationRoute) {
            AddServiceRoute() {
                navController.navigate(servicesListNavigationRoute)
            }
        }
    }

}