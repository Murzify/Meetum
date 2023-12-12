package com.murzify.meetum.core.domain.repository

import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {

    suspend fun getAllServices(): Flow<List<Service>>

    suspend fun addService(service: Service)

    suspend fun deleteService(service: Service)

    suspend fun editService(service: Service)

}