package com.murzify.meetum.core.common

import java.io.File

fun getAppData(): File {
    val os = System.getProperty("os.name").lowercase()

    val homePath = when {
        os.contains("win") -> System.getenv("APPDATA") ?: System.getProperty("user.home") // Windows
        os.contains("nix") || os.contains("nux") || os.contains("mac") -> System.getProperty("user.home") // MacOS and Linux
        else -> System.getProperty("user.dir") // Fallback to the current working directory
    }

    val appDataPath = "$homePath\\Meetum"
    val appDataFile = File(appDataPath)
    if (!appDataFile.exists()) appDataFile.mkdir()
    return appDataFile
}