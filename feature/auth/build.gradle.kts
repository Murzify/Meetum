import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("feature")
}

commonMainDependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.firebase.auth)
    implementation(libs.kottie)
}