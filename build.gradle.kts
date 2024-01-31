buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.resources.generator)
    }
}

plugins {
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.serialization)
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.sentry)
    alias(libs.plugins.google.services)
    id("com.github.gmazzo.buildconfig") version "5.3.1"
}

sentry {
    includeSourceContext = true
    org = "murzify"
    projectName = "meetum"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}