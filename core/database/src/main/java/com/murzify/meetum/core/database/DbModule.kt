package com.murzify.meetum.core.database

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.RecordDaoImpl
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.dao.ServiceDaoImpl
import com.murzify.meetum.`meetum-database`
import org.koin.dsl.module

val databaseModule = module {
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
                            val dateId = Uuid.randomUUID().toString()
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
        `meetum-database`(get()).recordsQueries
    }
    single {
        `meetum-database`(get()).recordDatesQueries
    }
    single {
        `meetum-database`(get()).servicesQueries
    }
    single<RecordDao> {
        RecordDaoImpl(get(), get())
    }
    single<ServiceDao>{
        ServiceDaoImpl(get())
    }

}