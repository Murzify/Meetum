plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("meetum-database") {
            packageName.set("com.murzify.meetum")
        }
    }
}

kotlin {
    androidTarget()
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.common)

                implementation(libs.uuid)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.coroutines)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.koin.core)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.android)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.jvm)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.database"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
