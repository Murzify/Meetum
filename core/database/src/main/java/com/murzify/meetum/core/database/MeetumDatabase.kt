package com.murzify.meetum.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.murzify.meetum.core.database.model.RecordDatesEntity
import com.murzify.meetum.core.database.model.RecordEntity
import com.murzify.meetum.core.database.model.ServiceEntity
import java.util.UUID

@Database(
    entities = [RecordEntity::class, ServiceEntity::class, RecordDatesEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(1,2)
    ]
)
@TypeConverters(Converters::class)
abstract class MeetumDatabase: RoomDatabase() {

}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `record_dates` " +
                "(`date_id` TEXT NOT NULL, `record_id` TEXT NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`date_id`), FOREIGN KEY(record_id) REFERENCES records(record_id) ON DELETE CASCADE)")

        val cursor = database.query("SELECT * FROM records")

        while (cursor.moveToNext()) {
            val recordId = cursor.getString(cursor.getColumnIndexOrThrow("record_id"))
            val time = cursor.getLong(cursor.getColumnIndexOrThrow("time"))

            val dateId = UUID.randomUUID().toString()
            database.execSQL("INSERT INTO `record_dates` (`date_id`, `record_id`, `date`) VALUES ('$dateId', '$recordId', $time)")

        }
        cursor.close()
        with(database) {
            execSQL("CREATE TABLE records_backup (`record_id` TEXT NOT NULL, `client_name` TEXT, `description` TEXT, `phone` TEXT, `service_id` TEXT NOT NULL, PRIMARY KEY(`record_id`))")
            execSQL("INSERT INTO `records_backup` SELECT `record_id`, `client_name`, `description`, `phone`, `service_id` FROM `records`")
            execSQL("DROP TABLE records")
            execSQL("ALTER TABLE records_backup RENAME to records")
        }
    }
}