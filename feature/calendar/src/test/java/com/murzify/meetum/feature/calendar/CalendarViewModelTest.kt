package com.murzify.meetum.feature.calendar

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.flow.Flow
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

    private lateinit var getRecordsUseCase: GetRecordsUseCase
    private lateinit var addRecordUseCase: AddRecordUseCase
    private lateinit var getServicesUseCase: GetServicesUseCase
    private lateinit var recordRepository: RecordRepository

    private lateinit var viewModel: CalendarViewModel

    private lateinit var testService: Service
    private lateinit var testRecord: Record
    private lateinit var flowServices: Flow<List<Service>>
    private lateinit var flowRecords: Flow<List<Record>>

    @Before
    fun init() = runTest {
        getRecordsUseCase = mock<GetRecordsUseCase>()
        addRecordUseCase = mock<AddRecordUseCase>()
        getServicesUseCase = mock<GetServicesUseCase>()
        recordRepository = mock<RecordRepository>()

        testService = Service(
            "haircut",
            100.0,
            Currency.getInstance("USD")
        )
        testRecord = Record(
            "Vasya",
            listOf(Date()),
            "some text",
            "+100000000000",
            testService
        )
        flowServices = flow {
            emit(emptyList<Service>())
        }
        flowRecords = flow {
            emit(emptyList<Record>())
        }
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