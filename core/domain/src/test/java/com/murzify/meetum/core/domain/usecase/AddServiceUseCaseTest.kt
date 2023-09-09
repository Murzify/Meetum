package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Currency

class AddServiceUseCaseTest {

    private val serviceRepository: ServiceRepository = mock()

    @Test
    fun `should call addService method`() = runTest {
        val useCase = AddServiceUseCase(serviceRepository)
        val testService = Service(
            "",
            0.0,
            Currency.getInstance("USD")
        )

        useCase(testService)
        verify(serviceRepository).addService(testService)
    }

}