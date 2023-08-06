package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import javax.inject.Inject

class AddServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(service: Service) = serviceRepository.addService(service)

}