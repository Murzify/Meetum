import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

configure<KotlinMultiplatformExtension> {
    jvm("desktop")
    if (pluginManager.hasPlugin("com.android.library")) {
        androidTarget()
    }

}