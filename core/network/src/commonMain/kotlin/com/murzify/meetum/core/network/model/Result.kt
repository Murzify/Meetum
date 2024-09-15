package com.murzify.meetum.core.network.model

sealed class Result() {
    data class Success<T>(val content: T): Result()

    data class Error<T>(val error: T): Result()
}