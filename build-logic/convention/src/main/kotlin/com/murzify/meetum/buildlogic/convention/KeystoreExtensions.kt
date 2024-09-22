package com.murzify.meetum.buildlogic.convention

import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

val Project.keystore: Properties
    get() {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        return keystoreProperties
    }