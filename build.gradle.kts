// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}
buildscript {
    repositories {
        mavenCentral()
        google()
    }

}
true // Needed to make the Suppress annotation work for the plugins block