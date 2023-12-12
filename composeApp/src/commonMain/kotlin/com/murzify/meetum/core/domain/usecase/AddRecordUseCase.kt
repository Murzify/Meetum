package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AddRecordUseCase constructor(
    private val recordRepository: RecordRepository
) {

    suspend operator fun invoke(record: Record) {
        recordRepository.addRecord(record)
    }

    suspend operator fun invoke(record: Record, repeat: Repeat) {
        if (repeat.repeatTimes == null && repeat.repeatToDate == null)
            throw IllegalArgumentException("end date is not passed")
        val dates = record.time.toMutableList()
        val startTime = record.time.first()

        val tz = TimeZone.currentSystemDefault()
        var currentDate = startTime.plus(1, DateTimeUnit.DAY, tz)
        while ((repeat.repeatToDate != null && currentDate <= repeat.repeatToDate!!) ||
            (repeat.repeatTimes != null && dates.size < repeat.repeatTimes!!)) {

            if (repeat.period == DateTimeUnit.WEEK) {
                val ldt= currentDate.toLocalDateTime(tz)
                if (ldt.dayOfWeek in repeat.daysOfWeek) {
                     dates.add(currentDate)
                }
                if (ldt.dayOfWeek == repeat.daysOfWeek.last()) {
                    val firstInWeek = dates[dates.size - repeat.daysOfWeek.size]
                    currentDate = firstInWeek
                        .plus(repeat.periodCount, DateTimeUnit.WEEK, tz)
                        .minus(1, DateTimeUnit.DAY, tz)
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY, tz)
                continue
            }

            currentDate = currentDate.plus(repeat.periodCount, repeat.period, tz)
            dates.add(currentDate)
        }

        val newRecord = record.copy(
            time = dates
        )
        recordRepository.addRecord(newRecord)
    }

}