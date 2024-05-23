package com.murzify.meetum.core.domain.usecase

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.repository.RecordRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AddRecordUseCase(
    private val recordRepository: RecordRepository
) {

    suspend operator fun invoke(record: Record) {
        recordRepository.addRecord(record)
    }

    suspend operator fun invoke(record: Record, repeat: Repeat) {
        if (repeat.repeatTimes == null && repeat.repeatToDate == null)
            throw IllegalArgumentException("end date is not passed")
        val dates = record.dates.toMutableList()
        val startTime = record.dates.first()

        val tz = TimeZone.currentSystemDefault()
        var currentDate = startTime.time

        while ((repeat.repeatToDate != null && currentDate <= repeat.repeatToDate!!) ||
            (repeat.repeatTimes != null && dates.size < repeat.repeatTimes!!)) {

            if (repeat.period == DateTimeUnit.WEEK) {
                val ldt= currentDate.toLocalDateTime(tz)
                if (ldt.dayOfWeek in repeat.daysOfWeek) {
                     dates.add(RecordTime(Uuid.randomUUID(), currentDate))
                }
                if (ldt.dayOfWeek == repeat.daysOfWeek.last()) {
                    val firstInWeek = dates[dates.size - repeat.daysOfWeek.size]
                    currentDate = firstInWeek
                        .time
                        .plus(repeat.periodCount, DateTimeUnit.WEEK, tz)
                        .minus(1, DateTimeUnit.DAY, tz)
                }
                currentDate = currentDate.plus(1, DateTimeUnit.DAY, tz)
                continue
            }

            currentDate = currentDate.plus(repeat.periodCount, repeat.period, tz)
            dates.add(RecordTime(Uuid.randomUUID(), currentDate))
        }

        val newRecord = record.copy(
            dates = dates
        )
        recordRepository.addRecord(newRecord)
    }

}