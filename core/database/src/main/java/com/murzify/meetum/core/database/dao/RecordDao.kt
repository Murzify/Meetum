package com.murzify.meetum.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.database.model.RecordDatesEntity
import com.murzify.meetum.core.database.model.RecordEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

@Dao
interface RecordDao {
    @Transaction
    @Query("SELECT * FROM records")
    fun getAll(): Flow<List<FullRecord>>

    @Transaction
    @Query("SELECT * FROM records WHERE EXISTS (SELECT 1 FROM record_dates WHERE record_dates.record_id = records.record_id AND record_dates.date BETWEEN :startDate AND :endDate)")
    fun getByDate(startDate: Date, endDate: Date): Flow<List<FullRecord>>

    @Insert
    fun add(record: RecordEntity)

    @Insert
    fun addDate(vararg recordDates: RecordDatesEntity)

    @Update
    fun update(record: RecordEntity)

    @Delete
    fun delete(record: RecordEntity)

    @Transaction
    @Query("SELECT * FROM records WHERE service_id == :serviceId AND EXISTS (SELECT 1 FROM record_dates WHERE record_dates.record_id == records.record_id AND record_dates.date > :currentTime)")
    fun getFuture(serviceId: UUID, currentTime: Date): List<FullRecord>

    @Query("DELETE FROM records WHERE service_id == :serviceId")
    fun deleteLinkedWithService(serviceId: UUID)

}