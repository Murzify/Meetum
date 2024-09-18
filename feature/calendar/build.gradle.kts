plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.common)
                implementation(projects.core.ui)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(libs.calendar)
                implementation(libs.window.size)
                implementation(libs.decompose)
                implementation(libs.decompose.extensions)
                implementation(libs.koin.core)
                implementation(libs.kottie)
                implementation(libs.kotlinx.serialization)
                implementation(libs.uuid)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.feature.calendar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
