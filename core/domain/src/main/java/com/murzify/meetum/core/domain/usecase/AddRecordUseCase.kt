package com.murzify.meetum.core.domain.usecase

import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.repository.RecordRepository
import java.util.Calendar
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
            repeat.repeatTimes?.let { times ->
                while (dates.size < times) {
                    var added = false
                    while (!added) {
                        if (repeat.period == Calendar.WEEK_OF_MONTH) {
                            repeat.daysOfWeek.forEach {  dayOfWeek ->
                                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                                if (timeInMillis > startTime.time) {
                                    dates.add(time)
                                    if (dates.size == times) {
                                        added = true
                                        return@forEach
                                    }
                                }
                            }
                            add(repeat.period, repeat.periodCount)
                            break
                        }
                        add(repeat.period, repeat.periodCount)
                        dates.add(time)
                        added = true
                    }
                }
            }
        }
        recordRepository.addRecord(record)
    }

}