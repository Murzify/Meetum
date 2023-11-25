package com.murzify.meetum.core.database.dao

import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.model.FullRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.util.UUID

interface RecordDao {

    suspend fun getAll(): Flow<List<FullRecord>>

    suspend fun getByDate(startDate: Instant, endDate: Instant): Flow<List<FullRecord>>

    suspend fun add(record: Records, dates: List<Instant>)

    suspend fun addDate(vararg recordDates: Record_dates)

    suspend fun update(record: Records)

    suspend fun delete(record: Records)

    suspend fun getFuture(serviceId: UUID, currentTime: Instant): List<FullRecord>

    suspend fun deleteLinkedWithService(serviceId: UUID)

    suspend fun deleteDate(recordId: UUID, date: Instant)

}