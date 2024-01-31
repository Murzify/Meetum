package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID


class RecordRepositoryImpl constructor(
    private val recordDao: RecordDao,
): RecordRepository {

    override suspend fun getAllRecords() = recordDao.getAll().map { recordList ->
        recordList.mapToRecord()
    }

    override suspend fun getRecords(starDate: Instant, endDate: Instant) = recordDao
        .getByDate(starDate, endDate).map { recordList ->
            recordList.mapToRecord()
        }

    override suspend fun futureRecords(serviceId: UUID): List<Record> {
        return recordDao.getFuture(serviceId, Clock.System.now()).mapToRecord()
    }

    override suspend fun deleteLinkedRecords(serviceId: UUID) {
        recordDao.deleteLinkedWithService(serviceId)
    }

    override suspend fun deleteDate(recordId: UUID, date: Instant) {
        recordDao.deleteDate(recordId, date)
    }

    override suspend fun addRecord(record: Record) {
        recordDao.add(record.toEntity(), record.time)
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.update(record.toEntity())
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.delete(record.toEntity())
    }

}