package com.murzify.meetum.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.usecase.AddRecordUseCase
import com.murzify.meetum.core.domain.usecase.GetRecordsUseCase
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
): ViewModel() {

    private val _records: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())
    val records: StateFlow<List<Record>> = _records

    fun addRecord(record: Record) {
        viewModelScope.launch(Dispatchers.IO) {
            addRecordUseCase(record)
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


}