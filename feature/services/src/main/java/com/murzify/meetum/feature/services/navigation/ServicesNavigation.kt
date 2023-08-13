package com.murzify.meetum.feature.services.navigation

import android.util.Log
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.murzify.meetum.feature.services.AddServiceRoute
import com.murzify.meetum.feature.services.ServicesListRoute
import com.murzify.meetum.feature.services.ServicesViewModel

const val servicesNavigationRoute = "services"
const val servicesListNavigationRoute = "servicesList"
const val addServiceNavigationRoute = "addService?editing={editing}"

fun NavGraphBuilder.servicesScreen(navController: NavHostController) {

    navigation(route = servicesNavigationRoute, startDestination = servicesListNavigationRoute) {
        composable(route = servicesListNavigationRoute) {
            ServicesListRoute(
                navigateToAddService = { edit ->
                    navController.navigate("addService?editing=$edit")
                }
            )
        }
        composable(
            route = addServiceNavigationRoute,
            arguments = listOf(
                navArgument("editing") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isEditing = backStackEntry.arguments?.getBoolean("editing") ?: false
            val servicesListViewModel: ServicesViewModel = if (isEditing) {
                val servicesListEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(servicesListNavigationRoute)
                }
                Log.d("addService", "editing")
                hiltViewModel(servicesListEntry)
            } else {
                Log.d("addService", "no editing")
                hiltViewModel()
            }

//            val servicesListEntry = remember(backStackEntry) {
//                navController.getBackStackEntry(servicesNavigationRoute)
//            }
//            Log.d("addService", "editing")
//            val servicesListViewModel = hiltViewModel<ServicesViewModel>(servicesListEntry)

            val prevRoute = navController.previousBackStackEntry?.destination?.route
            AddServiceRoute(
                viewModel = servicesListViewModel,
                isEditing =  isEditing
            ) {
                prevRoute?.let { navController.popBackStack(prevRoute, inclusive = false) }
            }
        }
    }

}