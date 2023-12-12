package com.murzify.meetum.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.database.ServicesQueries
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.flow.Flow

class ServiceDaoImpl(
    private val servicesQueries: ServicesQueries
) : ServiceDao {
    override fun getAll(): Flow<List<Services>> = servicesQueries
        .getAll()
        .asFlow()
        .mapToList(meetumDispatchers.io)


    override fun add(service: Services) {
        servicesQueries.add(service)
    }

    override fun edit(service: Services) {
        servicesQueries.edit(
            service.name,
            service.price,
            service.currency,
            service.service_id
        )
    }

    override fun delete(service: Services) {
        servicesQueries.delete(service.service_id)
    }
}