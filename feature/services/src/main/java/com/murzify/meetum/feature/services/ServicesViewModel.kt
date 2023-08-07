package com.murzify.meetum.feature.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murzify.meetum.core.domain.model.Service
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
    private val addServicesUseCase: AddServiceUseCase
): ViewModel() {

    private val _services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())
    val services: StateFlow<List<Service>> = _services

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

}