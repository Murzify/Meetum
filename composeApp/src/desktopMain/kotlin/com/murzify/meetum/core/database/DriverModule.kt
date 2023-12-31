package com.murzify.meetum.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module
import java.io.File
import java.util.*


actual val driverModule = module {
    single<SqlDriver> {
        val os = System.getProperty("os.name").lowercase()

        val homePath = when {
            os.contains("win") -> System.getenv("APPDATA") ?: System.getProperty("user.home") // Windows
            os.contains("nix") || os.contains("nux") || os.contains("mac") -> System.getProperty("user.home") // MacOS and Linux
            else -> System.getProperty("user.dir") // Fallback to the current working directory
        }

        val appDataPath = "$homePath\\Meetum"
        val appDataFile = File(appDataPath)
        if (!appDataFile.exists()) appDataFile.mkdir()

        JdbcSqliteDriver(
            "jdbc:sqlite:$appDataPath\\meetum.db",
            Properties(),
            schema = `meetum-database`.Schema,
            callbacks = arrayOf(
                AFTER_2_MIGRATION
            )
        )
    }
}