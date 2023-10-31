package com.murzify.meetum.core.domain.model

import java.util.Calendar
import java.util.Date

interface Repeat {
    val daysOfWeek: List<Int>
    val period: Int
    val periodCount: Int
    val repeatTimes: Int?
    val repeatToDate: Date?

    interface Builder {

        fun setDaysOfWeek(daysOfWeek: List<Int>): Builder

        fun every(every: Int, period: Int): Builder

        fun repeat(): RepeatRecord

    }
}

class RepeatRecord private constructor(
    override val daysOfWeek: List<Int>,
    override val period: Int,
    override val periodCount: Int,
    override val repeatTimes: Int?,
    override val repeatToDate: Date?
): Repeat {

    class Repeater: Repeat.Builder {

        private var daysOfWeek = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
        )

        private var period: Int = Calendar.DATE
        private var periodCount: Int = 1
        private var repeatTimes: Int? = null
        private var repeatToDate: Date? = null

        override fun setDaysOfWeek(daysOfWeek: List<Int>) = apply {
            this.daysOfWeek = daysOfWeek
        }

        override fun every(every: Int, period: Int) = apply {
            this.periodCount = every
            this.period = period
            Calendar.Builder()
        }

        fun end(times: Int) = apply {
            this.repeatTimes = times
        }

        fun end(date: Date) = apply {
            this.repeatToDate = date
        }

        override fun repeat(): RepeatRecord {

            return repeatRecord()
        }

        private fun repeatRecord() = RepeatRecord(
            daysOfWeek,
            period,
            periodCount,
            repeatTimes,
            repeatToDate
        )

    }

}
