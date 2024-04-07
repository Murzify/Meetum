package com.murzify.meetum.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.model.FirebaseBooking
import com.murzify.meetum.core.data.model.FirebaseBookingTime
import com.murzify.meetum.core.database.RecordDatesQueries
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.RecordsQueries
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class RecordDaoImpl(
    private val recordsQueries: RecordsQueries,
    private val recordDatesQueries: RecordDatesQueries
) : RecordDao {

    override val recordsForDeletion = recordsQueries
        .getForDeletion()
        .asFlow()
        .mapToList(meetumDispatchers.io)

    override val datesForDeletion = recordDatesQueries
        .getDeleted()
        .asFlow()
        .mapToList(meetumDispatchers.io)

    override val unsyncedRecords = recordsQueries
        .getUnsynced()
        .asFlow()
        .mapToList(meetumDispatchers.io)
        .map { entityList ->
            entityList.groupBy { it.record_id }.mapValues { (recordId, entityList) ->
                val first = entityList.first()
                val init = FirebaseBooking(
                    first.client_name,
                    first.description,
                    first.phone,
                    first.service_id,
                    time = mutableMapOf(),
                    first.deleted
                )
                entityList.fold(init) { acc, entity ->
                    val map = acc.time.toMutableMap()
                    map[entity.date_id] = FirebaseBookingTime(
                        entity.date,
                        entity.deleted_
                    )
                    acc.copy(
                        time = map
                    )
                }
            }
        }

    override suspend fun getAll(): Flow<List<FullRecord>> = recordsQueries
        .getAllRecords(mapper = ::FullRecord)
        .asFlow()
        .mapToList(meetumDispatchers.io)

    override suspend fun getByDate(startDate: Instant, endDate: Instant): Flow<List<FullRecord>> = recordsQueries
        .getByDate(
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds(),
            mapper = ::FullRecord
        )
        .asFlow()
        .mapToList(meetumDispatchers.io)

    override suspend fun add(record: Records, dates: List<Record_dates>) {
        recordDatesQueries.transaction {
            recordsQueries.add(record)
            dates.forEach { date ->
                recordDatesQueries.add(date)
            }
        }
    }

    override suspend fun addOrReplace(record: Records) {
        recordsQueries.addOrReplace(record)
    }

    override suspend fun addDate(vararg recordDates: Record_dates) {
        recordDatesQueries.transaction {
            recordDates.forEach {
                recordDatesQueries.add(it)
            }
        }
    }

    override suspend fun addOrReplaceDate(vararg recordDates: Record_dates) {
        recordDatesQueries.transaction {
            recordDates.forEach {
                recordDatesQueries.addOrReplace(it)
            }
        }
    }

    override suspend fun updateDate(vararg recordDates: Record_dates) {
        recordDatesQueries.transaction {
            recordDates.forEach {
                recordDatesQueries.update(it.date, it.deleted, it.date_id)
            }
        }
    }

    override suspend fun syncDates(recordId: String, recordDates: List<Record_dates>) {
        recordDatesQueries.transaction {
            recordDates.forEach {
                recordDatesQueries.update(it.date, it.deleted, it.date_id)
            }
        }
    }

    override suspend fun update(record: Records) {
        recordsQueries.update(
            record.client_name,
            record.description,
            record.phone,
            record.service_id,
            record.deleted,
            record.record_id
        )
    }

    override suspend fun delete(record: Records) {
        recordsQueries.delete(record.record_id)
    }

    override suspend fun markForDeletion(record: Records) {
        recordsQueries.markForDeleteion(record.record_id)
    }

    override suspend fun getFuture(serviceId: Uuid, currentTime: Instant): List<FullRecord> = recordsQueries
        .getFuture(
            currentTime.toEpochMilliseconds(),
            serviceId.toString(),
            mapper = ::FullRecord
        )
        .executeAsList()

    override suspend fun deleteLinkedWithService(serviceId: Uuid) {
        recordsQueries.markAsDeletedByService(serviceId.toString())
    }

    override suspend fun deleteDate(recordId: Uuid, date: Instant) {
        recordDatesQueries.delete(recordId.toString(), date.toEpochMilliseconds())
    }

    override suspend fun deleteDate(dateId: String) {
        recordDatesQueries.deleteById(dateId)
    }

    override suspend fun markDateForDeletion(dateId: String) {
        recordDatesQueries.markAsDeleted(dateId)
    }

    override suspend fun syncRecord(record: Records) {
        recordsQueries.add(record)
    }

}