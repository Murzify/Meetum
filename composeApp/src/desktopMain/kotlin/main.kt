import Meetum.composeApp.BuildConfig
import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.google.firebase.FirebasePlatform
import com.murzify.meetum.MR
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.data.repository.dataModule
import com.murzify.meetum.core.database.databaseModule
import com.murzify.meetum.core.database.driverModule
import com.murzify.meetum.core.datastore.dataStoreModule
import com.murzify.meetum.core.di.domainModule
import com.murzify.meetum.core.network.networkModule
import com.murzify.meetum.core.ui.MeetumTheme
import com.murzify.meetum.initSentry
import com.murzify.meetum.root.RealRootComponent
import com.murzify.meetum.root.RootUi
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.Koin
import java.awt.Dimension

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalResourceApi::class)
fun main() = application {
    val koin = createKoin()
    initSentry()
    firebaseInit(koin)
    Napier.base(DebugAntilog())
    val componentFactory = koin.get<ComponentFactory>()
    val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(lifecycle)
    val rootComponent = RealRootComponent(
        componentContext = componentContext,
        componentFactory,
        koin.get()
    )

    Window(
        title = stringResource(MR.strings.app_title),
        icon = painterResource("drawable/ic_launcher.png"),
        onCloseRequest = ::exitApplication
    ) {
        window.minimumSize = Dimension(800, 600)
        MeetumTheme() {
            isSystemInDarkTheme()
            rootComponent.onCalcWindow(calculateWindowSizeClass())
            RootUi(rootComponent)
        }
    }
}

private fun firebaseInit(koin: Koin) {
    val firebasePlatform = koin.get<FirebasePlatform>()
    FirebasePlatform.initializeFirebasePlatform(firebasePlatform)
    val options = FirebaseOptions(
        applicationId =  BuildConfig.APP_ID,
        apiKey =  BuildConfig.API_KEY,
        projectId = BuildConfig.PROJECT_ID,
        databaseUrl = BuildConfig.DB_URL
    )
    Firebase.initialize(Application(), options)
}

private fun createKoin() = Koin().apply {
    loadModules(
        listOf(databaseModule, dataModule, domainModule, driverModule, dataStoreModule, networkModule)
    )
    declare(ComponentFactory(this))
    createEagerInstances()
}
