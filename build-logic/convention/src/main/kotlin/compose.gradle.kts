import com.murzify.meetum.buildlogic.convention.androidMainDependencies
import com.murzify.meetum.buildlogic.convention.commonMainDependencies
import com.murzify.meetum.buildlogic.convention.desktopMainDependencies

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("kmp")
}

commonMainDependencies { kmpExtension ->
    kmpExtension.apply {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.components.uiToolingPreview)
    }
}

androidMainDependencies {
    implementation(libs.ui)
    implementation(libs.activity.compose)
    implementation(it.compose.preview)
}

desktopMainDependencies { kmpExtension ->
    implementation("org.jetbrains.compose.ui:ui-tooling-preview-desktop:1.7.0-rc01:")
}