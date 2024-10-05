import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("feature")
}

commonMainDependencies {
    implementation(libs.calendar)
    implementation(libs.kottie)
    implementation(libs.uuid)
    implementation(libs.napier)
}