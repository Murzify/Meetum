package com.murzify.meetum

import android.app.Application
import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.data.dataModule
import com.murzify.meetum.core.database.databaseModule
import com.murzify.meetum.core.database.driverModule
import com.murzify.meetum.core.datastore.dataStoreModule
import com.murzify.meetum.core.di.domainModule
import com.murzify.meetum.core.network.networkModule
import com.murzify.meetum.di.KoinProvider
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.Koin

class App: Application(), KoinProvider {

    override lateinit var koin: Koin
        private set

    override fun onCreate() {
        super.onCreate()
        initSentry()
        Napier.base(DebugAntilog())
        Firebase.database.setPersistenceEnabled(false)
        koin = createKoin()
    }

    private fun createKoin() = Koin().apply {
        loadModules(
            listOf(databaseModule, dataModule, domainModule, driverModule, dataStoreModule, networkModule)
        )
        declare(this@App as Application)
        declare(this@App as Context)
        declare(ComponentFactory(this))
        createEagerInstances()
    }
}