package com.murzify.meetum.core.database.dao

import com.murzify.meetum.core.database.Services
import kotlinx.coroutines.flow.Flow

interface ServiceDao {
    fun getAll(): Flow<List<Services>>

    fun add(service: Services)

    fun edit(service: Services)

    fun delete(service: Services)

}