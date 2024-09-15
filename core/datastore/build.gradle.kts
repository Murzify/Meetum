plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.com.android.library)
}

kotlin {
    androidTarget()
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.common)
                implementation(libs.datastore.prefs)
                implementation(libs.firebase.database)
                implementation(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.datastore"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
