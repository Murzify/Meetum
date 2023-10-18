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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.murzify.meetum.feature.calendar.ui.CalendarUi
import com.murzify.meetum.feature.services.ui.ServicesUi
import com.murzify.meetum.navigation.Screen

@Composable
fun RootUi(
    component: RootComponent,
) {
    val shouldShowBottomBar by component.shouldShowBottomBar.collectAsState()
    val shouldShowNavRail by component.shouldShowNavRail.collectAsState()
    val screensList = listOf(Screen.Calendar, Screen.Services)
    val childStack by component.childStack.collectAsState()
    val selectedScreen = childStack.active.instance.toScreen()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(modifier = Modifier.fillMaxWidth()) {
                    screensList.forEach { screen ->
                        NavigationBarItem(
                            selected = selectedScreen == screen,
                            onClick = { component.onTabSelected(screen) },
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
            if (shouldShowNavRail) {
                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                    screensList.forEach { screen ->
                        NavigationRailItem(
                            selected = selectedScreen == screen,
                            onClick = { component.onTabSelected(screen) },
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

            Children(
                childStack,
                animation = stackAnimation(fade()),
                modifier = Modifier.consumeWindowInsets(paddingValues).padding(paddingValues)
            ){
                when (val instance = it.instance) {
                    is RootComponent.Child.Calendar -> CalendarUi(instance.component)
                    is RootComponent.Child.Services -> ServicesUi(instance.component)
                }
            }
            
        }
    }
}

private fun RootComponent.Child.toScreen() = when (this) {
    is RootComponent.Child.Calendar -> Screen.Calendar
    is RootComponent.Child.Services -> Screen.Services
}



