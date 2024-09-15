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
                implementation(projects.core.domain)
                implementation(projects.core.common)
                implementation(projects.core.database)
                implementation(projects.core.network)

                implementation(libs.uuid)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.coroutines)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.ktor.client.core)
                implementation(libs.firebase.database)
                implementation(libs.firebase.auth)
                implementation(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
