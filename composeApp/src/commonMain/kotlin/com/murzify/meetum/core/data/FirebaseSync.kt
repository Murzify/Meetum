package com.murzify.meetum.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow

open class FirebaseSync {

    protected val auth = Firebase.auth
    protected val db: FirebaseDatabase = Firebase.database

    suspend fun FirebaseAuth.getUid(block: suspend (uid: String) -> Unit) {
        authStateChanged.collect { user ->
            user?.uid?.let { uid ->
                block(uid)
            }
        }
    }

    suspend fun <T> Flow<List<T>>.sync(forEach: suspend (T, uid: String) -> Unit) {
        auth.getUid { uid ->
            collect{ list ->
                list.forEach {
                    forEach(it, uid)
                }
            }
        }
    }
}