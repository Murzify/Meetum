import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("kmp")
}

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines)
            implementation(libs.kotlinx.serialization)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
        }
    }
}