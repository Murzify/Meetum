package com.murzify.meetum.core.common

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

fun <T> ComponentContext.registerKeeper(strategy: SerializationStrategy<T>, supplier: () -> T?) {
    stateKeeper.register("STATE", strategy, supplier)
}

fun <T: Any> ComponentContext.restore(strategy: DeserializationStrategy<T>): T? {
    return stateKeeper.consume("STATE", strategy)
}