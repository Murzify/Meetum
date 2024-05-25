plugins {
    id("meetum.android-lib")
    id("meetum.kmp")
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("meetum-database") {
            packageName.set("com.murzify.meetum.core.database")
        }
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.common)
                implementation(libs.koin.core)
                implementation(libs.coroutines)
                implementation(libs.uuid)
                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.coroutines)
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

android.namespace = "com.murzify.meetum.core.database"