package com.murzify.meetum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.di.koin
import com.murzify.meetum.ui.theme.MeetumTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val componentFactory = application.koin.get<ComponentFactory>()
        val componentContext = defaultComponentContext()
        val rootComponent = RealRootComponent(
            componentContext = componentContext,
            componentFactory
        )
        setContent {
            MeetumTheme {
                rootComponent.onCalcWindow(calculateWindowSizeClass())
                RootUi(component = rootComponent)
            }
        }
    }
}



