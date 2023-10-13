package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.repository.RecordRepository
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AddRecordUseCase @Inject constructor(
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
                var added = false
                while (!added) {
                    addWeekRepeat(repeat, startTime, ::shouldAddDate) {
                        dates.add(time)
                    }
                    add(repeat.period, repeat.periodCount)
                    if (repeat.period == Calendar.WEEK_OF_MONTH) {
                        break
                    }
                    dates.add(time)
                    added = true
                }
            }
        }
        recordRepository.addRecord(record)

    }

    private inline fun Calendar.addWeekRepeat(
        repeat: Repeat,
        startTime: Date,
        shouldAddDate: () -> Boolean,
        block: (Boolean) -> Unit
    ) {
        repeat.daysOfWeek.forEach { dayOfWeek ->
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            if (timeInMillis > startTime.time) {
                if (shouldAddDate()) {
                    block(true)
                    return@forEach
                } else {
                    block(false)
                }
            }
        }
    }

    private fun shouldAddDate(repeat: Repeat, dates: List<Date>): Boolean {
        return if (repeat.repeatTimes != null) {
            val times = repeat.repeatTimes!!
            (dates.size < times)
        } else if (repeat.repeatToDate != null) {
            (dates.last().time < repeat.repeatToDate!!.time)
        } else {
            false
        }
    }
}