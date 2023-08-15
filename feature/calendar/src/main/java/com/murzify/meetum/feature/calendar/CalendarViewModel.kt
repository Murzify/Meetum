package com.murzify.meetum.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.RecordRepository
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
import com.murzify.meetum.core.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getRecordsUseCase: GetRecordsUseCase,
    private val addRecordUseCase: AddRecordUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val recordRepository: RecordRepository,
): ViewModel() {

    private val _records: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())
    val records: StateFlow<List<Record>> = _records

    private val _services: MutableStateFlow<List<Service>> = MutableStateFlow(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _selectedRecord: MutableStateFlow<Record?> = MutableStateFlow(null)
    val selectedRecord: StateFlow<Record?> = _selectedRecord

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getServicesUseCase()
                .collect {
                    _services.value = it
                }
        }
    }

    fun addRecord(record: Record) {
        viewModelScope.launch(Dispatchers.IO) {
            addRecordUseCase(record)
        }
    }

    fun editRecord(record: Record) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.updateRecord(record)
        }
    }

    fun deleteRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedRecord.value?.let {
                recordRepository.deleteRecord(it)
            }
        }
    }

    fun getRecords(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            getRecordsUseCase(date)
                .collect {
                    _records.value = it
                }
        }
    }

    fun selectRecordForEdit(record: Record) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedRecord.value = record
        }
    }

}