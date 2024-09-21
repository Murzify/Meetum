import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("core")
}

commonMainDependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.network)

    implementation(libs.uuid)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.ktor.client.core)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
}
