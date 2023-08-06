package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsServiceRepository(repo: ServiceRepositoryImpl): ServiceRepository

    @Binds
    fun bindsRecordRepository(repo: RecordRepositoryImpl): RecordRepository

}