package com.murzify.meetum.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.murzify.meetum.core.database.model.RecordEntity
import com.murzify.meetum.core.database.model.RecordWithService
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RecordDao {
    @Query("SELECT * FROM records")
    fun getAll(): Flow<List<RecordWithService>>

    @Query("SELECT * FROM records WHERE :startDate <= time AND :endDate >= time")
    fun getByDate(startDate: Date, endDate: Date): Flow<List<RecordWithService>>

    @Insert
    fun add(record: RecordEntity)

    @Delete
    fun delete(record: RecordEntity)

}