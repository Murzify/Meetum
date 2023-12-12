package com.murzify.meetum.core.di

import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetRecordsUseCase(get()) }
    single { AddRecordUseCase(get()) }
    single { AddServiceUseCase(get()) }
    single { GetServicesUseCase(get()) }
}

