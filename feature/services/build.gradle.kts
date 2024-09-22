import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("feature")
}

commonMainDependencies {
    implementation(libs.firebase.auth)
    implementation(libs.uuid)
}
