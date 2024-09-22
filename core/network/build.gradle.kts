import com.murzify.meetum.buildlogic.convention.commonMainDependencies
import com.murzify.meetum.buildlogic.convention.keystore

plugins {
    alias(libs.plugins.serialization)
    id("com.github.gmazzo.buildconfig")
    id("core")
}

buildConfig {
    packageName("com.murzify.meetum.core.network")
    buildConfigField("API_KEY", keystore["apiKey"] as String )
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