package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface CalendarComponent {

    val navigateToAddService: () -> Unit
    val splitScreen: Boolean
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class RecordsManager(val component: RecordsManagerComponent): Child

        class AddRecord(val component: AddRecordComponent): Child

        class RecordInfo(val component: RecordInfoComponent): Child
    }
}