package com.murzify.meetum.core.datastore

import com.google.firebase.FirebasePlatform
import org.koin.dsl.module

val dataStoreModule = module {
    single {
        dataStore()
    }
    single<FirebasePlatform> {
        FirebasePlatformImpl(get())
    }
}