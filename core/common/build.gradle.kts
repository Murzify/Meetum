plugins {
    id("meetum.android-lib")
    id("meetum.kmp")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines)
                implementation(libs.decompose)
                implementation(libs.decompose.extensions)
            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.coroutines.swing)
            }
        }
    }
}

android.namespace = "com.murzify.meetum.core.common"