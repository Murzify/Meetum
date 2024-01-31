package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


class GetRecordsUseCase constructor(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(date: Instant): Flow<List<Record>> {
        val tz = TimeZone.currentSystemDefault()
        val ldt = date.toLocalDateTime(tz)
        val localDate = ldt.date
        val localTime = LocalTime(23, 59, 59, 0)
        val endDate = LocalDateTime(localDate, localTime).toInstant(tz)

        return recordRepository.getRecords(date, endDate)
    }

}