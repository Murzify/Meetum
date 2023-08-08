package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toDomain
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao
): ServiceRepository {
    override suspend fun getAllServices() = serviceDao.getAll().map { serviceList ->
        serviceList.map { it.toDomain() }
    }

    override suspend fun addService(service: Service) {
        serviceDao.add(service.toEntity())
    }

    override suspend fun deleteService(service: Service) {
        serviceDao.delete(service.toEntity())
    }

    override suspend fun editService(service: Service) {
        serviceDao.edit(service = service.toEntity())
    }

}