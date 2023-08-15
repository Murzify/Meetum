package com.murzify.meetum.core.domain.repository

import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RecordRepository {

    suspend fun getAllRecords(): Flow<List<Record>>

    suspend fun addRecord(record: Record)

    suspend fun updateRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    suspend fun getRecords(starDate: Date, endDate: Date): Flow<List<Record>>
}