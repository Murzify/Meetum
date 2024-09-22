package com.murzify.meetum.buildlogic.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.commonMainDependencies(block: KotlinDependencyHandler.(kmpExtension: KotlinMultiplatformExtension) -> Unit) {
    kmpConfig {
        sourceSets.commonMain.dependencies{
            block(this@kmpConfig)
        }
    }
}

fun Project.androidMainDependencies(block: KotlinDependencyHandler.(kmpExtension: KotlinMultiplatformExtension) -> Unit) {
    kmpConfig {
        sourceSets.androidMain.dependencies{
            block(this@kmpConfig)
        }
    }
}

fun Project.desktopMainDependencies(block: KotlinDependencyHandler.(kmpExtension: KotlinMultiplatformExtension) -> Unit) {
    kmpConfig {
        sourceSets.getByName("desktopMain").dependencies{
            block(this@kmpConfig)
        }
    }
}