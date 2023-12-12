package com.murzify.meetum

import io.sentry.kotlin.multiplatform.Sentry

fun initSentry() {
    Sentry.init {
        it.dsn = "https://740fcc3f22ddc1f838a1fe1419860a65@o4506211231203328.ingest.sentry.io/4506381931053056"
        it.tracesSampleRate = 1.0
    }
}