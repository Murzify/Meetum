package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

class RealRepetitiveEventsComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
    private val finish: (result: Repeat) -> Unit
) : RepetitiveEventsComponent, ComponentContext by componentContext {
    override val everyAmount = MutableStateFlow(1)
    override val everyPeriod = MutableStateFlow(Calendar.DATE)
    override val daysOfWeek: MutableStateFlow<List<DayOfWeek>> = MutableStateFlow(
        listOf()
    )
    override val showDaysOfWeek = MutableStateFlow(false)
    override val endTimes = MutableStateFlow(3)
    override val endDate = MutableStateFlow(
        Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DATE, 3)
        }.time
    )
    override val endType = MutableStateFlow<RepetitiveEventsComponent.EndType>(
        RepetitiveEventsComponent.EndType.Times
    )
    override val showDatePicker = MutableStateFlow(false)

    override fun onEveryAmountChanged(amount: String) {
        if (amount == "" || amount == "0") {
            everyAmount.value = 1
            return
        }
        everyAmount.value = amount.toInt()
    }

    override fun onPeriodChanged(period: Int) {
        val allowedPeriods = listOf(
            Calendar.DATE,
            Calendar.WEEK_OF_MONTH,
            Calendar.MONTH,
            Calendar.YEAR
        )
        if (period !in allowedPeriods) {
            throw IllegalStateException()
        }
        showDaysOfWeek.value = (period == Calendar.WEEK_OF_MONTH)
        everyPeriod.value = period
    }

    override fun onEndTimesChanged(times: String) {
        if (times.length > 3) return
        try {
            endTimes.value = if (times == "" || times.toInt() == 0) {
                1
            } else {
                times.toInt()
            }
        } catch (_: NumberFormatException) {}
    }

    override fun ondEndDateChanged(date: Date) {
        endDate.value = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time
    }

    override fun onEndTypeChanged(endType: RepetitiveEventsComponent.EndType) {
        this.endType.value = endType
    }

    override fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
        val newDaysOfWeek = daysOfWeek.value.toMutableList()
        if (daysOfWeek.value.contains(dayOfWeek)) {
            newDaysOfWeek.remove(dayOfWeek)
        } else {
            newDaysOfWeek.add(dayOfWeek)
        }
        daysOfWeek.value = newDaysOfWeek
    }

    override fun onPickDateClicked() {
        showDatePicker.value = true
    }

    override fun onDatePickerCancel() {
        showDatePicker.value = false
    }

    override fun onDatePickerOk(date: Date?) {
        showDatePicker.value = false
        if (date != null) {
            endDate.value = date
        }
    }

    override fun onSaveClicked() {
        val repeat: Repeat = RepeatRecord.Repeater()
            .every(everyAmount.value, everyPeriod.value)
            .setDaysOfWeek(
                daysOfWeek.value.map { it.toCalendar() }
            )
            .apply {
                when (endType.value) {
                    RepetitiveEventsComponent.EndType.Date -> end(endDate.value)
                    RepetitiveEventsComponent.EndType.Times -> end(endTimes.value)
                }
            }
            .repeat()
        finish(repeat)
    }

    override fun onBackClicked() {
        navigateBack()
    }

    private fun DayOfWeek.toCalendar(): Int {
        return when (this) {
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
        }
    }
 }
