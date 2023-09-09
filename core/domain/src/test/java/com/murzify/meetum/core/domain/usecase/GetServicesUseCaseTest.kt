package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import java.util.Currency

class GetServicesUseCaseTest {

    private val serviceRepository: ServiceRepository = mock()

    @Test
    fun `should return the same data that in repository`() = runTest {
        val testService = Service(
            "",
            0.0,
            Currency.getInstance("USD")
        )
        val serviceList = List(5) { testService }
        val recordsListFlow = flow {
            emit(serviceList)
        }

        Mockito.`when`(serviceRepository.getAllServices()).thenReturn(recordsListFlow)
        val useCase = GetServicesUseCase(serviceRepository)
        val actual = useCase()
        Assert.assertEquals(serviceList, actual.first())
    }

}