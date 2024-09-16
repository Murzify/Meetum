plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.murzify.meetum.core.ui.resources"
    generateResClass = auto
}

kotlin {
    androidTarget()
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(libs.calendar)
                implementation(libs.window.size)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ui)
                implementation(libs.activity.compose)
                implementation(libs.lottie.compose)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}