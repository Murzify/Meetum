package com.murzify.meetum

import kotlinx.coroutines.CoroutineDispatcher

interface MeetumDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

expect val meetumDispatchers: MeetumDispatchers