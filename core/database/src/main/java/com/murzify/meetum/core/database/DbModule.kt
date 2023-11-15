package com.murzify.meetum.core.database

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.RecordDaoImpl
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module
import java.util.UUID

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
    single<SqlDriver> {
        `meetum-database`.Schema
        AndroidSqliteDriver(
            `meetum-database`.Schema,
            get(),
            "meetum-database",
            callback = object : AndroidSqliteDriver.Callback(
                `meetum-database`.Schema,
                AfterVersion(2) { driver ->
                    driver.executeQuery(
                        null,
                        "SELECT record_id, time FROM records;",
                        mapper = { cursor ->
                            val recordId = cursor.getString(0)
                            val time = cursor.getLong(1)
                            val dateId = UUID.randomUUID().toString()
                            driver.execute(null, "INSERT INTO `record_dates` (`date_id`, `record_id`, `date`) VALUES ('$dateId', '$recordId', $time);", 3)
                            cursor.next()
                        },
                        0,
                        null
                    )
                    driver.execute(null, "ALTER TABLE records DROP COLUMN time", 0)
                }
            ) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
    single {
        val driver: SqlDriver = get()
        `meetum-database`(driver).recordsQueries
    }
    single {
        val driver: SqlDriver = get()
        `meetum-database`(driver).recordDatesQueries
    }
    single<RecordDao> {
        RecordDaoImpl(get(), get())
    }

}