package com.murzify.meetum.build_logic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FeaturePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply("meetum.koin")
                apply("com.google.gms.google-services")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:common"))

                add("implementation", libs.findLibrary("core.ktx").get())
                add("implementation", libs.findLibrary("lifecycle.runtime.ktx").get())
                add("implementation", libs.findLibrary("activity.compose").get())
                add("implementation", platform(libs.findLibrary("compose.bom").get()))
                add("implementation", libs.findLibrary("ui").get())
                add("implementation", libs.findLibrary("ui.graphics").get())
                add("implementation", libs.findLibrary("ui.tooling.preview").get())
                add("implementation", libs.findLibrary("material3").get())
                add("implementation", libs.findLibrary("decompose").get())
                add("implementation", libs.findLibrary("decompose-extensions").get())
                add("implementation", libs.findLibrary("kotlinx-serialization").get())
                add("implementation", libs.findLibrary("kotlinx-datetime").get())
            }
        }
    }
}