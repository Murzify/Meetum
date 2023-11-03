package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.repository.RecordRepository
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        Calendar.getInstance(Locale.getDefault()).apply {
            time = startTime
            fun shouldAddDate() = shouldAddDate(repeat, dates)

            while (shouldAddDate()) {
                while (true) {
                    addWeekRepeat(repeat, startTime, ::shouldAddDate) {
                        if (repeat.repeatTimes != null || time <= repeat.repeatToDate)
                            dates.add(time)
                    }
                    add(repeat.period, repeat.periodCount)
                    if (repeat.period == Calendar.WEEK_OF_MONTH) {
                        break
                    }
                    if (repeat.repeatTimes != null || time <= repeat.repeatToDate)
                        dates.add(time)
                    break
                }
            }
        }
        val newRecord = record.copy(
            time = dates
        )
        recordRepository.addRecord(newRecord)
    }

    private inline fun Calendar.addWeekRepeat(
        repeat: Repeat,
        startTime: Date,
        shouldAddDate: () -> Boolean,
        block: () -> Unit
    ) {
        repeat.daysOfWeek.forEach { dayOfWeek ->
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            if (timeInMillis > startTime.time) {
                if (shouldAddDate()) {
                    block()
                    return@forEach
                }
            }
        }
    }

    private fun Calendar.shouldAddDate(repeat: Repeat, dates: List<Date>): Boolean {
        return if (repeat.repeatTimes != null) {
            val times = repeat.repeatTimes!!
            (dates.size < times)
        } else if (repeat.repeatToDate != null) {
            val endDate = Calendar.getInstance().apply {
                time = repeat.repeatToDate
            }
            (dates.last().time < repeat.repeatToDate!!.time
                    && this <= endDate)
        } else {
            false
        }
    }
}