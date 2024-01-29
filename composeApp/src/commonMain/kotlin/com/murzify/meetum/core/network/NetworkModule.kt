package com.murzify.meetum.core.network

import org.koin.dsl.module

val networkModule = module {
    single { FirebaseAuth() }
}