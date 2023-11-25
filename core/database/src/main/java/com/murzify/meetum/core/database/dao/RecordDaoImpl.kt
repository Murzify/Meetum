package com.murzify.meetum.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.murzify.meetum.core.database.RecordDatesQueries
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.RecordsQueries
import com.murzify.meetum.core.database.model.FullRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.util.UUID

class RecordDaoImpl(
    private val recordsQueries: RecordsQueries,
    private val recordDatesQueries: RecordDatesQueries
) : RecordDao {
    override suspend fun getAll(): Flow<List<FullRecord>> = recordsQueries
        .getAllRecords(mapper = ::FullRecord)
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun getByDate(startDate: Instant, endDate: Instant): Flow<List<FullRecord>> = recordsQueries
        .getByDate(
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds(),
            mapper = ::FullRecord
        )
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun add(record: Records, dates: List<Instant>) {
        recordDatesQueries.transaction {
            recordsQueries.add(record)
            dates.forEach { date ->
                val recordDate = Record_dates(
                    UUID.randomUUID().toString(),
                    record.record_id,
                    date.toEpochMilliseconds()
                )
                recordDatesQueries.add(recordDate)
            }
        }
    }

    override suspend fun addDate(vararg recordDates: Record_dates) {
        recordDatesQueries.transaction {
            recordDates.forEach {
                recordDatesQueries.add(it)
            }
        }
    }

    override suspend fun update(record: Records) {
        recordsQueries.update(
            record.client_name,
            record.description,
            record.phone,
            record.service_id,
            record.record_id
        )
    }

    override suspend fun delete(record: Records) {
        recordsQueries.delete(record.record_id)
    }

    override suspend fun getFuture(serviceId: UUID, currentTime: Instant): List<FullRecord> = recordsQueries
        .getFuture(
            currentTime.toEpochMilliseconds(),
            serviceId.toString(),
            mapper = ::FullRecord
        )
        .executeAsList()

    override suspend fun deleteLinkedWithService(serviceId: UUID) {
        recordsQueries.deleteLinkedWithSerivce(serviceId.toString())
    }

    override suspend fun deleteDate(recordId: UUID, date: Instant) {
        recordDatesQueries.delete(recordId.toString(), date.toEpochMilliseconds())
    }
}