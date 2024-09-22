import com.murzify.meetum.buildlogic.convention.androidMainDependencies
import com.murzify.meetum.buildlogic.convention.commonMainDependencies

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
    }
}

androidMainDependencies {
    implementation(libs.ui)
    implementation(libs.activity.compose)
}