import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

configure<KotlinMultiplatformExtension> {
    androidTarget()
    jvm("desktop")
}

configure<LibraryExtension> {
    val module = project.path.split(":")
    namespace = "com.murzify.meeutm.${module[1]}.${module[2]}"

    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}