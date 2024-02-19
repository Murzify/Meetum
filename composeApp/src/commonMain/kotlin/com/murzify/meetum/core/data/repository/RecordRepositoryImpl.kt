package com.murzify.meetum.core.data.repository

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.model.FirebaseBooking
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.Records
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.ChildEvent
import dev.gitlive.firebase.database.database
import io.github.aakira.napier.Napier
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
): RecordRepository {

    private val scope = CoroutineScope(meetumDispatchers.io)
    private val auth = Firebase.auth

    init {
        val job = Job()
        val syncScope = CoroutineScope(meetumDispatchers.io + job)
        scope.launch {
            auth.authStateChanged.collect { user ->
                job.children.forEach { it.cancelAndJoin() }
                user?.uid?.let {
                    syncScope.launch {
                        syncBookings(it)
                    }
                }
            }
        }
    }

    private suspend fun syncBookings(uid: String) {
        userEvents<FirebaseBooking>(uid, "booking") { uuid, booking, type ->
            val records = Records(
                uuid!!,
                booking.clientName,
                booking.description,
                booking.phone,
                booking.serviceId
            )
            when (type) {
                ChildEvent.Type.ADDED -> {
                    recordDao.syncRecord(records)
                    val dates = booking.time.map {
                        Record_dates(
                            it.key,
                            uuid,
                            it.value
                        )
                    }.toTypedArray()
                    recordDao.addDate(
                        *dates
                    )
                }
                ChildEvent.Type.CHANGED -> {
                    recordDao.update(records)
                    val dates = booking.time.map {
                        Record_dates(
                            it.key,
                            uuid,
                            it.value
                        )
                    }.toTypedArray()
                    recordDao.updateDate(*dates)
                }
                ChildEvent.Type.MOVED -> {}
                ChildEvent.Type.REMOVED -> recordDao.delete(records)
            }
        }
    }

    private suspend inline fun <reified T> userEvents(
        uid: String,
        path: String,
        crossinline block: suspend (key: String?, value: T, type: ChildEvent.Type) -> Unit
    ) {
        val ref = Firebase.database.reference("users/$uid/$path")
        ref.childEvents().collect {
            block(it.snapshot.key, it.snapshot.value<T>(), it.type)
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
        Napier.d("delete", null, "sql")
        val uid = auth.currentUser?.uid!!
        Firebase.database.reference("users/$uid/booking/$recordId/time/${recordTime.id}")
            .removeValue()
        recordDao.deleteDate(recordTime.id.toString())
    }

    override suspend fun addRecord(record: Record) {
        val uid = auth.currentUser?.uid!!
        val bookingRef =
            Firebase.database.reference("users/$uid/booking/${record.id}")
        bookingRef.setValue(
            FirebaseBooking(
                record.clientName,
                record.description,
                record.phone,
                record.service.id.toString(),
                record.dates.associate { it.id.toString() to it.time.toEpochMilliseconds() }
            )
        )
        recordDao.add(
            record.toEntity(),
            record.dates.map { it.toEntity(record.id) }
        )
    }

    override suspend fun updateRecord(record: Record) {
        val uid = auth.currentUser?.uid!!
        val bookingRef =
            Firebase.database.reference("users/$uid/booking/${record.id}")
        bookingRef.setValue(
            FirebaseBooking(
                record.clientName,
                record.description,
                record.phone,
                record.service.id.toString(),
                record.dates.associate { it.id.toString() to it.time.toEpochMilliseconds() }
            )
        )
        recordDao.update(record.toEntity())
        val dates = record.dates.map { it.toEntity(record.id)}.toTypedArray()
        recordDao.updateDate(*dates)
    }

    override suspend fun deleteRecord(record: Record) {
        val uid = auth.currentUser?.uid!!
        val bookingRef =
            Firebase.database.reference("users/$uid/booking/${record.id}")
        bookingRef.removeValue()
        recordDao.delete(record.toEntity())
    }

}