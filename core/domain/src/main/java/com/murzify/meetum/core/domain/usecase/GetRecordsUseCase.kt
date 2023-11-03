package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class GetRecordsUseCase constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(date: Date): Flow<List<Record>> {
        val endDate = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            time = date
            set(Calendar.HOUR, 23)
            set(Calendar.MINUTE, 59)
        }.time

        return recordRepository.getRecords(date, endDate)
    }

}