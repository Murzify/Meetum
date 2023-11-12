package com.murzify.meetum.core.database

import androidx.room.Room
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            MeetumDatabase::class.java,
            "meetum-database"
        ).addMigrations(MIGRATION_2_3).build()
    }
    single {
        val db = get<MeetumDatabase>()
        db.serviceDao()
    }
    single {
        val db = get<MeetumDatabase>()
        db.recordDao()
    }
    single {
        AndroidSqliteDriver(
            `meetum-database`.Schema,
            get(),
            "meetum.db"
        )
    }
}