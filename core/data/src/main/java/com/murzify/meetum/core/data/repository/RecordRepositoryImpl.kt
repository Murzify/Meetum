package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.map
import java.util.Currency
import java.util.Date
import java.util.UUID


class RecordRepositoryImpl constructor(
    private val recordDao: RecordDao,
): RecordRepository {

    override suspend fun getAllRecords() = recordDao.getAll().map { recordList ->
        recordList.mapToRecord()
    }

    override suspend fun getRecords(starDate: Date, endDate: Date) = recordDao
        .getByDate(starDate, endDate).map { recordList ->
            recordList.mapToRecord()
        }

    override suspend fun futureRecords(serviceId: UUID): List<Record> {
        return recordDao.getFuture(serviceId, Date()).mapToRecord()
    }

    override suspend fun deleteLinkedRecords(serviceId: UUID) {
        recordDao.deleteLinkedWithService(serviceId)
    }

    override suspend fun deleteDate(recordId: UUID, date: Date) {
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

    private fun List<FullRecord>.mapToRecord() = groupBy { it.record_id_ }
        .map { (id, records) ->
            val record = records.first()
            Record(
                clientName = record.client_name,
                time = records.map { Date(it.date) },
                description = record.description,
                phone = record.phone,
                service = Service(
                    name = record.name,
                    price = record.price,
                    currency = Currency.getInstance(record.currency),
                    id = UUID.fromString(record.service_id_)
                ),
                id = UUID.fromString(id),
            )
        }

}