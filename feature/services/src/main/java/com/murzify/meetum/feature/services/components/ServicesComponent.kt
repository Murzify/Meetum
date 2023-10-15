package com.murzify.meetum.feature.services.components

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface ServicesComponent {
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class ServicesList(component: ServicesListComponent) : Child

        class AddService(component: AddServiceComponent) : Child
    }
}