package com.murzify.meetum.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class Migrations {
    private val TEST_DB = "meetum-database"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MeetumDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate1to2() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO services (service_id, name, price, currency)" +
                    "VALUES(\"ab1743a1-7744-4eaa-8136-890e1851cc8b\", \"massage\", 50.0, \"USD\")")

            execSQL("INSERT INTO records (record_id, client_name, time, description, service_id)" +
                    "VALUES( \"e68bf281-8ff6-404a-aa2f-e2964d3dc3a1\", \"Jess\", 123123123, \"what??\", \"ab1743a1-7744-4eaa-8136-890e1851cc8b\")")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true)
    }

    @Test
    @Throws(IOException::class)
    fun migrate2to3() {
        var db = helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO services (service_id, name, price, currency)" +
                    "VALUES(\"ab1743a1-7744-4eaa-8136-890e1851cc8b\", \"massage\", 50.0, \"USD\")")

            execSQL("INSERT INTO records (record_id, client_name, time, description, service_id)" +
                    "VALUES( \"e68bf281-8ff6-404a-aa2f-e2964d3dc3a1\", \"Jess\", 123123123, \"what??\", \"ab1743a1-7744-4eaa-8136-890e1851cc8b\")")
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)
        db.query("SELECT * FROM record_dates").apply {
            moveToFirst()
            val date = getLong(getColumnIndexOrThrow("date"))
            Assert.assertEquals(123123123, date)
        }

    }
}