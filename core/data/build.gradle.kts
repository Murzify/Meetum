plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.database)
                implementation(projects.core.common)
                implementation(projects.core.network)
                implementation(libs.koin.core)
                implementation(libs.coroutines)
                implementation(libs.uuid)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.ktor.client.core)

                implementation(libs.firebase.auth)
                implementation(libs.firebase.database)
            }
        }
    }
}