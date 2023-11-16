package com.murzify.meetum.core.database.dao

import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.model.FullRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

interface RecordDao {

    suspend fun getAll(): Flow<List<FullRecord>>

    suspend fun getByDate(startDate: Date, endDate: Date): Flow<List<FullRecord>>

    suspend fun add(record: Records, dates: List<Date>)

    suspend fun addDate(vararg recordDates: Record_dates)

    suspend fun update(record: Records)

    suspend fun delete(record: Records)

    suspend fun getFuture(serviceId: UUID, currentTime: Date): List<FullRecord>

    suspend fun deleteLinkedWithService(serviceId: UUID)

    suspend fun deleteDate(recordId: UUID, date: Date)

}