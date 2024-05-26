plugins {
    id("meetum.android-lib")
    id("meetum.kmp")
    id("meetum.compose")
    id(libs.plugins.multiplatform.resources.get().pluginId)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(libs.coroutines)
                implementation(libs.kotlinx.datetime)
                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                implementation(libs.window.size)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.lottie.compose)
            }
        }
    }
}

multiplatformResources.multiplatformResourcesPackage = "com.murzify.meetum.core.ui"

android.namespace = "com.murzify.meetum.core.ui"