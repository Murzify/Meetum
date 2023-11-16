package com.murzify.meetum.core.data

import com.murzify.meetum.core.data.repository.RecordRepositoryImpl
import com.murzify.meetum.core.data.repository.mapToRecord
import com.murzify.meetum.core.database.Record_dates
import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.model.FullRecord
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.UUID

class RecordRepositoryImplTest {

    private val recordDao: RecordDao = mock()
    private lateinit var repo: RecordRepository

    private val testService = Service(
        "",
        0.0,
        Currency.getInstance("USD")
    )
    private val testRecord = Record(
        "Vasya",
        listOf(Date()),
        null,
        null,
        testService
    )
    private val testDate = Record_dates(
        UUID.randomUUID().toString(),
        testRecord.id.toString(),
        Date().time
    )
    private val testFullRecord = FullRecord(
        testRecord.id.toString(),
        testRecord.clientName,
        testRecord.description,
                testRecord.phone,
        testRecord.service.id.toString(),
                testService.id.toString(),
        testService.name,
        testService.price,
        testService.currency.currencyCode,
        testDate.date_id,
        testDate.record_id,
        testDate.date
    )
    private val recordsList = List(5) { testFullRecord }

    @Before
    fun setup() {
        repo = RecordRepositoryImpl(recordDao)
    }

    @Test
    fun `should return all records`() = runTest {
        val recordsFlow = flow {
            emit(recordsList)
        }
        Mockito.`when`(recordDao.getAll()).thenReturn(
            recordsFlow
        )
        assertEquals(
            repo.getAllRecords().first(),
            recordsFlow.first().mapToRecord()
        )
    }

    @Test
    fun `should return records by period`() = runTest {
        val recordsFlow = flow {
            emit(recordsList)
        }
        Mockito.`when`(recordDao.getByDate(any(), any())).thenReturn(recordsFlow)
        val startDate = Calendar.getInstance().apply {
            set(2023, 9, 5)
        }.toInstant()
        val endDate = Calendar.getInstance().apply {
            set(2023, 9, 1)
        }.toInstant()
        assertEquals(
            repo.getRecords(Date.from(startDate), Date.from(endDate)).first(),
            recordsFlow.first().mapToRecord()
        )
    }

    @Test
    fun `should call add method`() = runTest {
        repo.addRecord(testRecord)
        verify(recordDao).add(testRecord.toEntity(), testRecord.time)
    }

    @Test
    fun `should call update method`() = runTest {
        repo.updateRecord(testRecord)
        verify(recordDao).update(testRecord.toEntity())
    }

    @Test
    fun `should call delete method`() = runTest {
        repo.deleteRecord(testRecord)
        verify(recordDao).delete(testRecord.toEntity())
    }
}