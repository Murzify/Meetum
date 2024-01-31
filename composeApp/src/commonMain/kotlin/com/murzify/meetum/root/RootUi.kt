package com.murzify.meetum.root

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.murzify.meetum.feature.auth.ui.AuthUi
import com.murzify.meetum.feature.calendar.ui.CalendarUi
import com.murzify.meetum.feature.services.ui.ServicesUi
import com.murzify.meetum.root.RootComponent.Child
import com.murzify.meetum.root.navigation.Screen
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
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
                                    painter = painterResource(screen.iconPath),
                                    contentDescription = stringResource(
                                        screen.stringId
                                    )
                                )
                            },
                            label = {
                                Text(text = stringResource(screen.stringId))
                            },
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        Row(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            if (shouldShowNavRail) {
                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                    screensList.forEach { screen ->
                        NavigationRailItem(
                            selected = selectedScreen == screen,
                            onClick = { component.onTabSelected(screen) },
                            icon = {
                                Icon(
                                    painter = painterResource(screen.iconPath),
                                    contentDescription = stringResource(
                                        screen.stringId
                                    )
                                )
                            },
                            label = {
                                Text(text = stringResource(screen.stringId))
                            }
                        )
                    }
                }
            }

            val animOrientation = if (shouldShowNavRail)
                Orientation.Vertical else Orientation.Horizontal

            Children(
                childStack,
                animation = stackAnimation(fade()),
            ){
                when (val instance = it.instance) {
                    is Child.Auth -> AuthUi(instance.component, animOrientation)
                    is Child.Calendar -> CalendarUi(instance.component, animOrientation)
                    is Child.Services -> ServicesUi(instance.component, animOrientation)
                }
            }

        }
    }
}

private fun Child.toScreen() = when (this) {
    is Child.Calendar -> Screen.Calendar
    is Child.Services -> Screen.Services
    is Child.Auth -> null
}



