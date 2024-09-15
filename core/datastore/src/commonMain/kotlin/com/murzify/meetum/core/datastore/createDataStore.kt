package com.murzify.meetum.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(
    producePath: () -> String
): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { File(producePath()) }
)

internal const val dataStoreFileName = "meetum.preferences_pb"