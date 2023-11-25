package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.DayOfWeek
import java.util.Currency

class AddRecordUseCaseTest {

    private val recordRepository: RecordRepository = mock()
    private val testService = Service(
        "",
        0.0,
        Currency.getInstance("USD")
    )
    private val testRecord = Record(
        "Vasya",
        listOf(
            Clock.System.now()
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
        val end = LocalDateTime(2023, 12, 31, 17, 29, 0, 0)

        val repeat: Repeat = RepeatRecord.Repeater()
            .every(2, DateTimeUnit.WEEK)
            .setDaysOfWeek(listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
            .end(end.toInstant(TimeZone.currentSystemDefault()))
            .repeat()

        useCase(testRecord, repeat)
    }

}