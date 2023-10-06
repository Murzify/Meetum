package com.murzify.meetum.feature.calendar

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.util.Currency
import java.util.Date

class CalendarViewModelTest {

    private val getRecordsUseCase = mock<GetRecordsUseCase>()
    private val addRecordUseCase = mock<AddRecordUseCase>()
    private val getServicesUseCase = mock<GetServicesUseCase>()
    private val recordRepository = mock<RecordRepository>()

    private lateinit var viewModel: CalendarViewModel

    private val testService = Service(
        "haircut",
        100.0,
        Currency.getInstance("USD")
    )
    private val testRecord = Record(
        "Vasya",
        Date(),
        "some text",
        "+100000000000",
        testService
    )
    private val flowServices = flow {
        emit(emptyList<Service>())
    }
    private val flowRecords = flow {
        emit(emptyList<Record>())
    }

    @Before
    fun init() = runTest {
        Mockito.`when`(getServicesUseCase.invoke()).thenReturn(flowServices)
        Mockito.`when`(recordRepository.getAllRecords()).thenReturn(flowRecords)
        viewModel = CalendarViewModel(
            getRecordsUseCase,
            addRecordUseCase,
            getServicesUseCase,
            recordRepository
        )
    }

    @After
    fun reset() {
        Mockito.reset(
            getRecordsUseCase,
            addRecordUseCase,
            getServicesUseCase,
            recordRepository
        )
    }

    @Test
    fun `should add record`() = runTest {
        viewModel.addRecord(testRecord)
        verify(addRecordUseCase, times(1)).invoke(testRecord)
    }

    @Test
    fun `should select record`() = runTest {
        viewModel.selectRecordForEdit(testRecord)
        Assert.assertEquals(
            testRecord,
            viewModel.selectedRecord.first()
        )
    }

    @Test
    fun `should update record`() = runTest {
        viewModel.editRecord(testRecord)
        verify(recordRepository, times(1)).updateRecord(testRecord)
    }

    @Test
    fun `should delete record`() = runTest {
        viewModel.selectRecordForEdit(testRecord)
        viewModel.deleteRecord()
        verify(recordRepository, times(1)).deleteRecord(testRecord)
    }

    @Test
    fun `should get records by date and set to StateFlow`() = runTest {
        val currentTime = Date()
        val testRecordsFlow = flow {
            emit(listOf(testRecord))
        }
        testRecordsFlow.first()
        Mockito.`when`(getRecordsUseCase.invoke(any())).thenReturn(testRecordsFlow)
        viewModel.getRecords(currentTime)
        verify(getRecordsUseCase, times(1)).invoke(currentTime)

        Assert.assertEquals(
            listOf(testRecord),
            viewModel.records.value
        )
    }
}