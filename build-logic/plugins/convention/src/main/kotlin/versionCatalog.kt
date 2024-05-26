@file:Suppress("Filename")

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

val Project.compose: ComposePlugin.Dependencies
    get() = extensions.getByType<ComposeExtension>().dependencies