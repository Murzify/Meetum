plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.com.android.library)
}

kotlin {
    androidTarget()
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(libs.kotlinx.serialization)
                implementation(libs.coroutines)

                implementation(libs.decompose)
                implementation(libs.decompose.extensions)

                implementation(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
