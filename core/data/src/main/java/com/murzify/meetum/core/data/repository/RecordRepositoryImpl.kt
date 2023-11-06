package com.murzify.meetum.core.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.RecordDatesEntity
import com.murzify.meetum.core.database.model.toDomain
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID


class RecordRepositoryImpl constructor(
    private val recordDao: RecordDao,
): RecordRepository {

    override suspend fun getAllRecords() = recordDao.getAll().map { recordList ->
        recordList.map { it.toDomain() }
    }

    override suspend fun getRecords(starDate: Date, endDate: Date) = recordDao
        .getByDate(starDate, endDate).map { recordList ->
            recordList.map { it.toDomain() }
        }

    override suspend fun futureRecords(serviceId: UUID): List<Record> {
        return recordDao.getFuture(serviceId, Date()).map {
            it.toDomain()
        }
    }

    override suspend fun deleteLinkedRecords(serviceId: UUID) {
        recordDao.deleteLinkedWithService(serviceId)
    }

    override suspend fun deleteDate(recordId: UUID, date: Date) {
        recordDao.deleteDate(recordId, date)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun addRecord(record: Record) {
        recordDao.add(record.toEntity())
        val dates = record.time.map {
            RecordDatesEntity(
                recordId = record.id,
                date = it
            )
        }.toTypedArray()
        recordDao.addDate(*dates)
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.update(record.toEntity())
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.delete(record.toEntity())
    }

}