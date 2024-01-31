package com.murzify.meetum.root

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.core.domain.repository.FirebaseRepository
import com.murzify.meetum.feature.auth.components.createLoginComponent
import com.murzify.meetum.feature.calendar.components.createCalendarComponent
import com.murzify.meetum.feature.services.components.createServicesComponent
import com.murzify.meetum.meetumDispatchers
import com.murzify.meetum.root.navigation.Screen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
    private val firebaseRepo: FirebaseRepository
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val shouldShowBottomBar = MutableStateFlow(true)

    override val shouldShowNavRail = MutableStateFlow(false)

    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Calendar,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val splitScreen: Boolean get() = !shouldShowBottomBar.value

    private val auth = Firebase.auth

    private val coroutineScope = componentCoroutineScope()
    private lateinit var windowSizeClass: WindowSizeClass
    private var emailVerified: Boolean = true
    init {
        coroutineScope.launch {
            auth.currentUser?.reload()
            val idToken = auth.currentUser?.getIdToken(false)
            emailVerified = idToken?.let { firebaseRepo.getUserData(it).emailVerified } == true
            if (!emailVerified) {
                shouldShowNavRail.value = false
                shouldShowBottomBar.value = false
                navigation.replaceAll(ChildConfig.Auth)
            }
        }
    }

    override fun onTabSelected(screen: Screen) {
        val config = when (screen) {
            Screen.Calendar -> ChildConfig.Calendar
            Screen.Services -> ChildConfig.Services(false)
        }
        navigation.bringToFront(config)
    }

    override fun onCalcWindow(windowSizeClass: WindowSizeClass) {
        this.windowSizeClass = windowSizeClass
        coroutineScope.launch(meetumDispatchers.io) {
            if (emailVerified) {
                shouldShowBottomBar.value = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                shouldShowNavRail.value = !shouldShowBottomBar.value
            } else {
                shouldShowNavRail.value = false
                shouldShowBottomBar.value = false
            }
        }

    }

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config) {
        is ChildConfig.Auth -> RootComponent.Child.Auth(
            componentFactory.createLoginComponent(
                componentContext,
                navigateToCalendar = {
                    shouldShowBottomBar.value = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                    shouldShowNavRail.value = !shouldShowBottomBar.value
                    navigation.bringToFront(ChildConfig.Calendar)
                }
            )
        )
        ChildConfig.Calendar -> RootComponent.Child.Calendar(
            componentFactory.createCalendarComponent(componentContext, splitScreen) {
                navigation.push(ChildConfig.Services(true))
            }
        )
        is ChildConfig.Services -> RootComponent.Child.Services(
            componentFactory.createServicesComponent(
                componentContext,
                config.addService,
                navigateBack = navigation::pop
            )

        )
    }

    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object Auth : ChildConfig

        @Serializable
        data object Calendar : ChildConfig

        @Serializable
        data class Services(val addService: Boolean) : ChildConfig
    }
}