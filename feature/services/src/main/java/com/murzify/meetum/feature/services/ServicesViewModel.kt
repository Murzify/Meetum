package com.murzify.meetum.feature.services

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.core.domain.usecase.AddServiceUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val getServicesUseCase: GetServicesUseCase,
    private val addServicesUseCase: AddServiceUseCase,
    private val serviceRepository: ServiceRepository,
    private val recordRepository: RecordRepository
): ViewModel() {

    private val _services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _selectedService: MutableStateFlow<Service?> = MutableStateFlow(null)
    val selectedService: StateFlow<Service?> = _selectedService

    private val _futureRecords: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())
    val futureRecords: StateFlow<List<Record>> = _futureRecords

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect {
                    _services.value = it
                }
        }
    }

    fun addService(service: Service) {
        viewModelScope.launch(Dispatchers.IO) {
            addServicesUseCase(service)
        }
    }

    fun editService(service: Service) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceRepository.editService(service)
        }
    }

    fun deleteService(service: Service) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteLinkedRecords(service.id)
            serviceRepository.deleteService(service)
        }
    }

    fun getFutureRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedService.value?.let {
                _futureRecords.emit(
                    recordRepository.futureRecords(it.id)
                )
            }

        }
    }

    fun selectService(service: Service) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedService.value = service
            Log.d("addService", selectedService.value.toString())
        }
    }
}