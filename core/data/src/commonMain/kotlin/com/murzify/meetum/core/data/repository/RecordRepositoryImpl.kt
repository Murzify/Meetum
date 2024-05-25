package com.murzify.meetum.core.data.repository

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.common.meetumDispatchers
import com.murzify.meetum.core.data.FirebaseSync
import com.murzify.meetum.core.data.mapToRecord
import com.murzify.meetum.core.data.model.FirebaseBooking
import com.murzify.meetum.core.data.toFirebase
import com.murzify.meetum.core.data.userEvents
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.repository.RecordRepository
import dev.gitlive.firebase.database.ChildEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID


class RecordRepositoryImpl(
    private val recordDao: RecordDao,
): RecordRepository, FirebaseSync() {

    private val scope = CoroutineScope(meetumDispatchers.io)

    init {
        val job = Job()
        val syncScope = CoroutineScope(meetumDispatchers.io + job)
        scope.launch {
            auth.getUid { uid ->
                job.children.forEach { it.cancelAndJoin() }
                syncScope.launch {
                    syncBookings(uid)
                }
            }
        }

        scope.launch {
            recordDao.unsyncedRecords.toFirebase().sync { id, booking, uid ->
                db.reference("users/$uid/booking/${id}")
                    .setValue(
                        booking
                    )
            }
        }

    }

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

    override suspend fun deleteDate(recordTime: RecordTime, recordId: Uuid) {
        recordDao.markDateForDeletion(recordTime.id.toString())
    }

    override suspend fun addRecord(record: Record) {
        recordDao.add(
            record.toEntity(),
            record.dates.map { it.toEntity(record.id) }
        )
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.update(record.toEntity())
        val dates = record.dates.map { it.toEntity(record.id)}.toTypedArray()
        recordDao.updateDate(*dates)
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.markForDeletion(record.toEntity())
    }

    private suspend fun syncBookings(uid: String) {
        userEvents<FirebaseBooking>(uid, "booking") { uuid, booking, type ->
            val records = Records(
                uuid!!,
                booking.clientName,
                booking.description,
                booking.phone,
                booking.serviceId,
                deleted = booking.deleted,
                synced = true
            )
            when (type) {
                ChildEvent.Type.ADDED -> {
                    recordDao.addOrReplace(records)
                    val dates = booking.time.map {
                        Record_dates(
                            it.key,
                            uuid,
                            it.value.time,
                            deleted = it.value.deleted,
                            synced = true
                        )
                    }.toTypedArray()
                    recordDao.addOrReplaceDate(
                        *dates
                    )
                }
                ChildEvent.Type.CHANGED -> {
                    val dates = booking.time.map {
                        Record_dates(
                            it.key,
                            uuid,
                            it.value.time,
                            deleted = it.value.deleted,
                            synced = true
                        )
                    }
                    recordDao.syncDates(records.record_id, dates)
                    recordDao.update(records)
                }
                ChildEvent.Type.MOVED -> {}
                ChildEvent.Type.REMOVED -> {}
            }
        }
    }

}