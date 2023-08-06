package com.murzify.meetum.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.RecordEntity
import com.murzify.meetum.core.database.model.ServiceEntity

@Database(entities = [RecordEntity::class, ServiceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MeetumDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun serviceDao(): ServiceDao

}