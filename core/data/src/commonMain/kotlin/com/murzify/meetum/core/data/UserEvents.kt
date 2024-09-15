package com.murzify.meetum.core.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.ChildEvent
import dev.gitlive.firebase.database.database

suspend inline fun <reified T> userEvents(
    uid: String,
    path: String,
    crossinline block: suspend (key: String?, value: T, type: ChildEvent.Type) -> Unit
) {
    val ref = Firebase.database.reference("users/$uid/$path")
    ref.childEvents().collect {
        block(it.snapshot.key, it.snapshot.value<T>(), it.type)
    }
}