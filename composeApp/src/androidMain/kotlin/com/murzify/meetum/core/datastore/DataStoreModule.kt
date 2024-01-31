package com.murzify.meetum.core.datastore

import org.koin.dsl.module

val dataStoreModule = module {
    single {
        dataStore(get())
    }
}