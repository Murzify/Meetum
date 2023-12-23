package com.murzify.meetum.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module
import java.util.Properties


actual val driverModule = module {
    single<SqlDriver> {
        JdbcSqliteDriver(
            "jdbc:sqlite:meetum.db",
            Properties(),
            schema = `meetum-database`.Schema,
            callbacks = arrayOf(
                AFTER_2_MIGRATION
            )
        )
    }
}