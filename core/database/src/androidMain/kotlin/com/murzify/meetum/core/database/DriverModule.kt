package com.murzify.meetum.core.database

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module

actual val driverModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            `meetum-database`.Schema,
            get(),
            "meetum-database",
            callback = object : AndroidSqliteDriver.Callback(
                `meetum-database`.Schema,
                AFTER_2_MIGRATION
            ) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}