package com.murzify.meetum.core.database.dao

import com.murzify.meetum.core.database.Services
import kotlinx.coroutines.flow.Flow

interface ServiceDao {

    val servicesForDeletion: Flow<List<String>>

    val unsyncedServices: Flow<List<Services>>

    fun getAll(): Flow<List<Services>>

    fun add(service: Services)

    fun addOrReplace(service: Services)

    fun edit(service: Services)

    fun delete(service: Services)

    fun markForDeletion(serviceId: String)

}