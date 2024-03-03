package com.murzify.meetum.core.database.dao

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.database.GetForDeletion
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.model.FullRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface RecordDao {

    val recordsForDeletion: Flow<List<String>>

    val datesForDeletion: Flow<List<GetForDeletion>>

    val unsyncedRecords: Flow<List<FullRecord>>

    suspend fun getAll(): Flow<List<FullRecord>>

    suspend fun getByDate(startDate: Instant, endDate: Instant): Flow<List<FullRecord>>

    suspend fun add(record: Records, dates: List<Record_dates>)

    suspend fun addDate(vararg recordDates: Record_dates)

    suspend fun updateDate(vararg recordDates: Record_dates)

    suspend fun syncDates(recordId: String, recordDates: List<Record_dates>)

    suspend fun update(record: Records)

    suspend fun delete(record: Records)

    suspend fun markForDeletion(record: Records)

    suspend fun getFuture(serviceId: Uuid, currentTime: Instant): List<FullRecord>

    suspend fun deleteLinkedWithService(serviceId: Uuid)

    suspend fun deleteDate(recordId: Uuid, date: Instant)

    suspend fun markDateForDeletion(dateId: String)

    suspend fun deleteDate(dateId: String)

    suspend fun syncRecord(record: Records)

}