import com.murzify.meetum.buildlogic.convention.commonMainDependencies
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.serialization)
    id("com.github.gmazzo.buildconfig")
    id("core")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

buildConfig {
    packageName("com.murzify.meetum.kmp")
    buildConfigField("API_KEY", keystoreProperties["apiKey"] as String )
}

commonMainDependencies {
    implementation(projects.core.domain)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.logging)
    implementation(libs.napier)
}