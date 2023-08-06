package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke() = serviceRepository.getAllServices()

}