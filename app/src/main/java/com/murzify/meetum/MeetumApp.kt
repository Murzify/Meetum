package com.murzify.meetum

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.murzify.meetum.navigation.MeetumNavHost

@Composable
fun MeetumApp(
    windowSizeClass: WindowSizeClass,
    appState: AppState = rememberMeetumState(
        windowSizeClass = windowSizeClass
    )
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                NavigationBar(modifier = Modifier.fillMaxWidth()) {
                    appState.screensList.forEach { screen ->
                        NavigationBarItem(
                            selected = appState.currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { appState.navController.navigate(screen.route) {
                                popUpTo(appState.navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.iconId),
                                    contentDescription = stringResource(
                                        id = screen.stringId
                                    )
                                )
                            },
                            label = {
                                Text(text = stringResource(id = screen.stringId))
                            },
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Row {
            if (appState.shouldShowNavRail) {
                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                    appState.screensList.forEach { screen ->
                        NavigationRailItem(
                            selected = appState.currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { appState.navController.navigate(screen.route) {
                                popUpTo(appState.navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.iconId),
                                    contentDescription = stringResource(
                                        id = screen.stringId
                                    )
                                )
                            },
                            label = {
                                Text(text = stringResource(id = screen.stringId))
                            }
                        )
                    }
                }
            }
            MeetumNavHost(
                appState = appState,
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            )
        }
    }
}



