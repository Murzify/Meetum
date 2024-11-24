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
    implementation("androidx.compose.material3.adaptive:adaptive:1.0.0")
    implementation("androidx.compose.material3.adaptive:adaptive-layout:1.0.0")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.0.0")
}

android {
    buildFeatures {
        compose = true
    }
}