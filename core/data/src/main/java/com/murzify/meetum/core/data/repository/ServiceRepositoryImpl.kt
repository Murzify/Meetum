package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.map
import java.util.Currency
import java.util.UUID


class ServiceRepositoryImpl constructor(
    private val serviceDao: ServiceDao
): ServiceRepository {
    override suspend fun getAllServices() = serviceDao.getAll().map { serviceList ->
        serviceList.map {
            Service(
                it.name,
                it.price,
                Currency.getInstance(it.currency),
                UUID.fromString(it.service_id)
            )
        }
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