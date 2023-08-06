package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toDomain
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
    private val serviceDao: ServiceDao
): RecordRepository {

    override suspend fun getAllRecords() = recordDao.getAll().map { recordList ->
        recordList.map { it.toDomain() }
    }

    override suspend fun getRecords(starDate: Date, endDate: Date) = recordDao
        .getByDate(starDate, endDate).map { recordList ->
            recordList.map { it.toDomain() }
        }

    override suspend fun addRecord(record: Record) {
        // TODO delete
        serviceDao.add(record.service.toEntity())

        recordDao.add(record.toEntity())
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.delete(record.toEntity())
    }
}