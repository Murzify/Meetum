package com.murzify.meetum.core.data

import com.murzify.meetum.core.data.repository.FirebaseRepositoryImpl
import com.murzify.meetum.core.data.repository.RecordRepositoryImpl
import com.murzify.meetum.core.data.repository.ServiceRepositoryImpl
import com.murzify.meetum.core.domain.repository.FirebaseRepository
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
    single<FirebaseRepository> {
        FirebaseRepositoryImpl(get())
    }
}