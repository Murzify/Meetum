package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import java.util.Currency
import java.util.Date

class GetRecordsUseCaseTest {

    private val recordsRepository: RecordRepository = mock()

    @Test
    fun `should return the same data that in repository`() = runTest {
        val testService = Service(
            "",
            0.0,
            Currency.getInstance("USD")
        )
        val testRecord = Record(
            "Vasya",
            listOf(Clock.System.now()),
            null,
            "+10000000000",
            testService
        )
        val recordsList = List(5) { testRecord }
        val recordsListFlow = flow<List<Record>> {
            emit(recordsList)
        }

        Mockito.`when`(recordsRepository.getRecords(any(), any())).thenReturn(recordsListFlow)
        val useCase = GetRecordsUseCase(recordsRepository)
        val actual = useCase(Date())
        Assert.assertEquals(recordsList, actual.first())
    }

}