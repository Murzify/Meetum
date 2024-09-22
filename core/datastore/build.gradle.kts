import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("core")
}

commonMainDependencies {
    implementation(projects.core.common)

    implementation(libs.datastore.prefs)
    implementation(libs.firebase.database)
}