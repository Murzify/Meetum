import java.io.FileInputStream
import java.util.Properties

plugins {
    id("meetum.kmp")
    id("com.github.gmazzo.buildconfig")
    alias(libs.plugins.serialization)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

@Suppress("INLINE_FROM_HIGHER_PLATFORM")
buildConfig {
    buildConfigField("API_KEY", keystoreProperties["apiKey"] as String )
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
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.logging)
                implementation(libs.napier)
            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.logging.jvm)
            }
        }
    }
}