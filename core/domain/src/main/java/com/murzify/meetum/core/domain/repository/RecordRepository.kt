package com.murzify.meetum.core.domain.repository

import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.util.UUID

interface RecordRepository {

    suspend fun getAllRecords(): Flow<List<Record>>

    suspend fun addRecord(record: Record)

    suspend fun updateRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    suspend fun getRecords(starDate: Instant, endDate: Instant): Flow<List<Record>>

    suspend fun futureRecords(serviceId: UUID): List<Record>

    suspend fun deleteLinkedRecords(serviceId: UUID)

    suspend fun deleteDate(recordId: UUID, date: Instant)
}