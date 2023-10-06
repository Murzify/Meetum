package com.murzify.meetum.feature.services

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.verify
import java.util.Currency
import java.util.Date
import java.util.UUID

class ServiceViewModelTest {

    private lateinit var viewModel: ServicesViewModel
    private val getServicesUseCase = mock<GetServicesUseCase>()
    private val addServicesUseCase = mock<AddServiceUseCase>()
    private val serviceRepository = mock<ServiceRepository>()
    private val recordRepository = mock<RecordRepository>()

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

    @Before
    fun init() = runTest {
        Mockito.`when`(getServicesUseCase()).thenReturn(flowServices)
        viewModel = ServicesViewModel(
            getServicesUseCase,
            addServicesUseCase,
            serviceRepository,
            recordRepository
        )
    }

    @After
    fun reset() {
        Mockito.reset(
            getServicesUseCase,
            addServicesUseCase,
            serviceRepository,
            recordRepository
        )
    }

    @Test
    fun `should add service`() = runTest {
        viewModel.addService(testService)
        verify(addServicesUseCase).invoke(testService)
    }

    @Test
    fun `should update service`() = runTest {
        viewModel.editService(testService)
        verify(serviceRepository).editService(testService)
    }

    @Test
    fun `should delete service and related records with them`() = runTest {
        viewModel.deleteService(testService)
        verify(recordRepository).deleteLinkedRecords(testService.id)
        verify(serviceRepository).deleteService(testService)
    }

    @Test
    fun `should select service`() = runTest {
        viewModel.selectService(testService)
        Assert.assertEquals(
            testService,
            viewModel.selectedService.first()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should get future record and set it to StateFlow`() = runTest {
        val recordsList = listOf(
            testRecord,
            testRecord.copy(id = UUID.randomUUID())
        )
        Mockito.`when`(recordRepository.futureRecords(testService.id)).thenReturn(recordsList)
        viewModel.selectService(testService)
        viewModel.selectedService.first()
        viewModel.getFutureRecords()
        verify(recordRepository).futureRecords(any())
        Assert.assertEquals(
            recordsList,
            viewModel.futureRecords.first()
        )
    }

}