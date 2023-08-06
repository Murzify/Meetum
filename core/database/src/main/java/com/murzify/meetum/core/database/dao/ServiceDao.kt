package com.murzify.meetum.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.murzify.meetum.core.database.model.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAll(): Flow<List<ServiceEntity>>

    @Insert
    fun add(service: ServiceEntity)

    @Delete
    fun delete(service: ServiceEntity)

}