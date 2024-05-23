package com.murzify.meetum.core.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed interface Repeat {
    val daysOfWeek: List<DayOfWeek>
    val period: DateTimeUnit
    val periodCount: Int
    val repeatTimes: Int?
    val repeatToDate: Instant?

    @Serializable
    sealed interface Builder {
        fun setDaysOfWeek(daysOfWeek: List<DayOfWeek>): Builder
        fun every(every: Int, period: DateTimeUnit): Builder
        fun end(times: Int): Builder
        fun end(date: Instant): Builder
        fun repeat(): Repeat
    }
}

@Serializable
class RepeatRecord private constructor(
    override val daysOfWeek: List<DayOfWeek>,
    override val period: DateTimeUnit,
    override val periodCount: Int,
    override val repeatTimes: Int?,
    override val repeatToDate: Instant?
): Repeat {

    @Serializable
    class Repeater: Repeat.Builder {
        private var daysOfWeek = DayOfWeek.entries.toList()
        private var period: DateTimeUnit = DateTimeUnit.DAY
        private var periodCount: Int = 1
        private var repeatTimes: Int? = null
        private var repeatToDate: Instant? = null

        override fun setDaysOfWeek(daysOfWeek: List<DayOfWeek>) = apply {
            this.daysOfWeek = daysOfWeek
        }

        override fun every(every: Int, period: DateTimeUnit) = apply {
            this.periodCount = every
            this.period = period
        }

        override fun end(times: Int) = apply {
            this.repeatTimes = times
        }

        override fun end(date: Instant) = apply {
            this.repeatToDate = date
        }

        override fun repeat(): RepeatRecord {
            return RepeatRecord(
                daysOfWeek,
                period,
                periodCount,
                repeatTimes,
                repeatToDate
            )
        }
    }
}

