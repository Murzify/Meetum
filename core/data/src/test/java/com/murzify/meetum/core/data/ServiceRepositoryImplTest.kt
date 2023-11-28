package com.murzify.meetum.core.data

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.repository.ServiceRepositoryImpl
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Currency

class ServiceRepositoryImplTest {

    private val serviceDao: ServiceDao = mock()
    private lateinit var repo: ServiceRepository

    private val testService = Service(
        "",
        0.0,
        Currency.getInstance("USD")
    )
    private val servicesList = List(5) { testService.toEntity() }

    @Before
    fun setup() {
        repo = ServiceRepositoryImpl(serviceDao)
    }

    @Test
    fun `should return all services`() = runTest {
        val servicesFlow = flow {
            emit(servicesList)
        }
        Mockito.`when`(serviceDao.getAll()).thenReturn(
            servicesFlow
        )
        Assert.assertEquals(
            repo.getAllServices().first(),
            servicesFlow.first().map {
                Service(
                    it.name,
                    it.price,
                    Currency.getInstance(it.currency),
                    Uuid.fromString(it.service_id)
                )
            }
        )
    }

    @Test
    fun `should call add method`() = runTest {
        repo.addService(testService)
        verify(serviceDao).add(testService.toEntity())
    }

    @Test
    fun `should call edit method`() = runTest {
        repo.editService(testService)
        verify(serviceDao).edit(testService.toEntity())
    }

    @Test
    fun `should call delete method`() = runTest {
        repo.deleteService(testService)
        verify(serviceDao).delete(testService.toEntity())
    }
}