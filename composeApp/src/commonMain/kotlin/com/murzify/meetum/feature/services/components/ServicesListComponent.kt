package com.murzify.meetum.feature.services.components

import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.flow.StateFlow

interface ServicesListComponent {
    val services: StateFlow<List<Service>>
    val showGhostLottie: StateFlow<Boolean>

    fun onServiceClick(service: Service)

    fun onAddServiceClick()
}