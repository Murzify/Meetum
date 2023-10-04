package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Currency
import java.util.Date

class AddRecordUseCaseTest {

    private val recordRepository: RecordRepository = mock()

    @Test
    fun `should call addRecord method`() = runTest {
        val useCase = AddRecordUseCase(recordRepository)
        val testService = Service(
            "",
            0.0,
            Currency.getInstance("USD")
        )
        val testRecord = Record(
            "Vasya",
            Date(),
            null,
            "+10000000000",
            testService
        )
        useCase(testRecord)
        verify(recordRepository).addRecord(testRecord)
    }

}