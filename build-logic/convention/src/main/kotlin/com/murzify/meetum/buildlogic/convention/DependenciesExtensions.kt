package com.murzify.meetum.buildlogic.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.commonMainDependencies(block: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.commonMain.dependencies(block)
    }
}

fun Project.androidMainDependencies(block: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.androidMain.dependencies(block)
    }
}

fun Project.desktopMainDependencies(block: KotlinDependencyHandler.() -> Unit) {
    kotlinMultiplatformConfig {
        sourceSets.jvmMain.dependencies(block)
    }
}