package com.murzify.meetum.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.RecordEntity
import com.murzify.meetum.core.database.model.RecordWithService
import com.murzify.meetum.core.database.model.ServiceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DbTest {
    private lateinit var recordDao: RecordDao
    private lateinit var serviceDao: ServiceDao
    private lateinit var db: MeetumDatabase
    private val serviceId = UUID.randomUUID()
    private val testRecord = RecordEntity(
        UUID.randomUUID(),
        "Kate",
        Date(),
        "~~~",
        "+10000000000",
        serviceId
    )
    private val testService = ServiceEntity(
        serviceId,
        "massage",
        50.0,
        Currency.getInstance("USD"),
    )

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MeetumDatabase::class.java
        ).build()
        recordDao = db.recordDao()
        serviceDao = db.serviceDao()
        serviceDao.add(testService)
        recordDao.add(testRecord)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun addRecordAndReturn() = runTest {
        val actual = recordDao.getAll().first()[0]
        Assert.assertEquals(
            RecordWithService(
                testRecord,
                testService
            ),
            actual
        )
    }

    @Test
    fun updateRecord() = runTest {
        val updatedRecord = testRecord.copy(clientName = "Mikhail")
        recordDao.update(updatedRecord)
        val actual = recordDao.getAll().first()[0]
        Assert.assertEquals(
            RecordWithService(
                updatedRecord,
                testService
            ),
            actual
        )
    }

    @Test
    fun getRecordByDate() = runTest {
        val calendar = Calendar.getInstance().apply {
            time = testRecord.time
        }
        repeat(3) {
            calendar.add(Calendar.DATE, 1)
            recordDao.add(
                testRecord.copy(recordId = UUID.randomUUID() , time = calendar.time)
            )
        }
        calendar.apply {
            time = testRecord.time
            add(Calendar.DATE, 2)
        }
        val actual = recordDao.getByDate(testRecord.time, calendar.time).first()
        Assert.assertEquals(3, actual.size)
        Assert.assertEquals(actual[0].record.time, testRecord.time)
        Assert.assertEquals(actual[2].record.time, calendar.time)
    }

    @Test
    fun getFutureRecords() = runTest {
        val calendar = Calendar.getInstance().apply {
            time = testRecord.time
        }
        repeat(3) {
            calendar.add(Calendar.DATE, 1)
            recordDao.add(
                testRecord.copy(recordId = UUID.randomUUID() , time = calendar.time)
            )
        }
        val currentTime = calendar.apply {
            time = testRecord.time
            add(Calendar.DATE, 1)
        }.time

        val actual = recordDao.getFuture(testService.serviceId, currentTime)
        Assert.assertEquals(
            2, actual.size
        )

        val start = calendar.apply {
            time = currentTime
            add(Calendar.DATE, 1)
        }.time
        val end = calendar.apply {
            time = start
            add(Calendar.DATE, 1)
        }.time
        Assert.assertEquals(
            start, actual[0].record.time
        )
        Assert.assertEquals(end, actual.last().record.time)
    }

    @Test
    fun deleteOneRecord() = runTest {
        val deletableRecord = testRecord.copy(recordId = UUID.randomUUID())
        recordDao.add(deletableRecord)
        recordDao.delete(deletableRecord)
        val actual = recordDao.getAll().first()
        Assert.assertEquals(
            false,
            actual.contains(RecordWithService(deletableRecord, testService))
        )
    }

    @Test
    fun deleteAllRelatedRecordsWithService() = runTest {
        val newService = testService.copy(serviceId = UUID.randomUUID())
        val newRecord = testRecord.copy(
            recordId = UUID.randomUUID(),
            serviceId = newService.serviceId
        )
        serviceDao.add(newService)
        recordDao.add(
            newRecord
        )
        recordDao.deleteLinkedWithService(newService.serviceId)
        val actual = recordDao.getAll().first()
        Assert.assertEquals(
            false,
            actual.contains(RecordWithService(newRecord, newService))
        )
    }

    @Test
    fun returnService() = runTest {
        val actual = serviceDao.getAll().first()
        Assert.assertEquals(
            testService,
            actual[0]
        )
    }

    @Test
    fun editService() = runTest {
        val newService = testService.copy(name = "haircut")
        serviceDao.edit(
            newService
        )
        val actual = serviceDao.getAll().first().first()
        Assert.assertEquals(
            newService,
            actual
        )
    }

    @Test
    fun deleteService() = runTest {
        serviceDao.delete(testService)
        Assert.assertEquals(
            false,
            serviceDao.getAll().first().contains(testService)
        )
    }
}