package com.murzify.meetum.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.FirebasePlatform
import com.murzify.meetum.meetumDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FirebasePlatformImpl(
    private val dataStore: DataStore<Preferences>
): FirebasePlatform() {

    private val scope = CoroutineScope(meetumDispatchers.io + SupervisorJob())

    override fun clear(key: String) {
        scope.launch {
            dataStore.edit {
                it[stringPreferencesKey(key)] = ""
            }
        }
    }

    override fun log(msg: String) {
        println(msg)
    }

    override fun retrieve(key: String): String? = runBlocking {
        dataStore.data.first()[stringPreferencesKey(key)]
    }

    override fun store(key: String, value: String) {
        scope.launch {
            dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
    }
}