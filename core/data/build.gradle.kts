plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    jvm("desktop")
    sourceSets {
        val commonMain by getting {

        }
    }
}