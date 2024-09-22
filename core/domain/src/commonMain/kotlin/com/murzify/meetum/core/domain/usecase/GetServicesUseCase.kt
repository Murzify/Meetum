package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.repository.ServiceRepository


class GetServicesUseCase constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke() = serviceRepository.getAllServices()

}