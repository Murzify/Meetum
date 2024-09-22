import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    id("compose")
}

commonMainDependencies { kmpExtension ->
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))

    implementation(kmpExtension.compose.components.resources)
    implementation(libs.kotlinx.serialization)
    implementation(libs.window.size)
    implementation(libs.decompose)
    implementation(libs.decompose.extensions)
    implementation(libs.koin.core)
}