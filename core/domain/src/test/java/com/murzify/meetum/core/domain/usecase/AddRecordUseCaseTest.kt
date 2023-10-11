package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Calendar
import java.util.Currency
import java.util.Date

class AddRecordUseCaseTest {

    private val recordRepository: RecordRepository = mock()
    val testService = Service(
        "",
        0.0,
        Currency.getInstance("USD")
    )
    val testRecord = Record(
        "Vasya",
        listOf(
            Calendar.getInstance().apply {
                time = Date()
                set(Calendar.DAY_OF_MONTH, 31)
            }.time
        ),
        null,
        "+10000000000",
        testService
    )
    @Test
    fun `should call addRecord method`() = runTest {
        val useCase = AddRecordUseCase(recordRepository)

        useCase(testRecord)
        verify(recordRepository).addRecord(testRecord)
    }

    @Test
    fun `should repeat records correctly`() = runTest {
        val useCase = AddRecordUseCase(recordRepository)

        val repeat: Repeat = RepeatRecord.Repeater()
            .every(1, Calendar.MONTH)
            .end(4)
            .repeat()

        useCase(testRecord, repeat)
    }

}