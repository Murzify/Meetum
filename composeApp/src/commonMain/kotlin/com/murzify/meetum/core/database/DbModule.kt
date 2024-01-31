package com.murzify.meetum.core.database

import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.RecordDaoImpl
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.dao.ServiceDaoImpl
import com.murzify.meetum.`meetum-database`
import org.koin.core.module.Module
import org.koin.dsl.module

expect val driverModule: Module

val databaseModule = module {
    single {
        `meetum-database`(get()).recordsQueries
    }
    single {
        `meetum-database`(get()).recordDatesQueries
    }
    single {
        `meetum-database`(get()).servicesQueries
    }
    single<RecordDao> {
        RecordDaoImpl(get(), get())
    }
    single<ServiceDao>{
        ServiceDaoImpl(get())
    }

}