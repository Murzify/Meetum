import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.data.repository.dataModule
import com.murzify.meetum.core.database.databaseModule
import com.murzify.meetum.core.database.driverModule
import com.murzify.meetum.core.di.domainModule
import com.murzify.meetum.initSentry
import com.murzify.meetum.root.RealRootComponent
import com.murzify.meetum.root.RootUi
import org.koin.core.Koin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() = application {
    val koin = createKoin()
    initSentry()
    val componentFactory = koin.get<ComponentFactory>()
    val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(lifecycle)
    val rootComponent = RealRootComponent(
        componentContext = componentContext,
        componentFactory
    )
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme(colors = darkColors()) {
            isSystemInDarkTheme()
            rootComponent.onCalcWindow(calculateWindowSizeClass())
            RootUi(rootComponent)
        }
    }
}

private fun createKoin() = Koin().apply {
    loadModules(
        listOf(databaseModule, dataModule, domainModule, driverModule)
    )
    declare(ComponentFactory(this))
    createEagerInstances()
}
