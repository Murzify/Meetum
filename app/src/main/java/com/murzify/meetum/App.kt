package com.murzify.meetum

import android.app.Application
import android.content.Context
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.data.repository.dataModule
import com.murzify.meetum.core.database.databaseModule
import com.murzify.meetum.di.KoinProvider
import com.murzify.meetum.di.domainModule
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import org.koin.core.Koin

class App: Application(), KoinProvider {

    override lateinit var koin: Koin
        private set

    override fun onCreate() {
        super.onCreate()
        val config = AppMetricaConfig.newConfigBuilder(BuildConfig.APPMETRICA_KEY).build()
        AppMetrica.activate(this, config)
        koin = createKoin()
    }

    private fun createKoin() = Koin().apply {
        loadModules(
            listOf(databaseModule, dataModule, domainModule)
        )
        declare(this@App as Application)
        declare(this@App as Context)
        declare(ComponentFactory(this))
        createEagerInstances()
    }
}