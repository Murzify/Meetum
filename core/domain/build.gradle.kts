import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("core")
}

commonMainDependencies {
    implementation(libs.uuid)
    implementation(libs.sentry.kmp)
    implementation(libs.kotlinx.datetime)
}
