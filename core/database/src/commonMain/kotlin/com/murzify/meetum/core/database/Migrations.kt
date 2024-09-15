package com.murzify.meetum.core.database

import app.cash.sqldelight.db.AfterVersion
import com.benasher44.uuid.Uuid

val AFTER_2_MIGRATION = AfterVersion(2) { driver ->
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