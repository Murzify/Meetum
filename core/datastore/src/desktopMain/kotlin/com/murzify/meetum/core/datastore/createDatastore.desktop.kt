package com.murzify.meetum.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.murzify.meetum.core.common.getAppData
import java.io.File

fun dataStore(): DataStore<Preferences> =
    createDataStore {
        val appData = getAppData()
        File(appData, dataStoreFileName).path
    }