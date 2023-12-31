package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import org.koin.dsl.module

val dataModule = module {
    single<ServiceRepository> {
        ServiceRepositoryImpl(get())
    }
    single<RecordRepository> {
        RecordRepositoryImpl(get())
    }
}