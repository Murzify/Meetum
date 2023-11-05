package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.registerKeeper
import com.murzify.meetum.core.common.restore
import com.murzify.meetum.core.domain.model.Repeat
import com.murzify.meetum.core.domain.model.RepeatRecord
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent.EndType
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

class RealRepetitiveEventsComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
    private val finish: (result: Repeat) -> Unit
) : RepetitiveEventsComponent, ComponentContext by componentContext {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            everyAmount = 1,
            everyPeriod = Calendar.DATE,
            daysOfWeek = listOf(),
            showDaysOfWeek = false,
            endTimes = 3,
            endDate = Calendar.getInstance().apply {
                time = Date()
                add(Calendar.DATE, 3)
            }.time,
            endType = EndType.Times,
            showDatePicker = false
        )
    )

    init {
        registerKeeper(Model.serializer()) { model.value }
    }

    override fun onEveryAmountChanged(amount: String) {
        if (amount == "" || amount == "0") {
            model.update { it.copy(everyAmount = 1) }
            return
        }
        model.update { it.copy(everyAmount = amount.toInt()) }
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
        model.update {
            it.copy(
                showDaysOfWeek = period == Calendar.WEEK_OF_MONTH,
                everyPeriod = period
            )
        }
    }

    override fun onEndTimesChanged(times: String) {
        if (times.length > 3) return
        try {
            model.update {
                it.copy(
                    endTimes = if (times == "" || times.toInt() == 0) {
                        1
                    } else {
                        times.toInt()
                    }
                )
            }
        } catch (_: NumberFormatException) {}
    }

    override fun ondEndDateChanged(date: Date) {
        model.update {
            it.copy(
                endDate = Calendar.getInstance().apply {
                    time = date
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time
            )
        }
    }

    override fun onEndTypeChanged(endType: EndType) {
        model.update {
            it.copy(endType = endType)
        }
    }

    override fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
        val newDaysOfWeek = model.value.daysOfWeek.toMutableList()
        if (model.value.daysOfWeek.contains(dayOfWeek)) {
            newDaysOfWeek.remove(dayOfWeek)
        } else {
            newDaysOfWeek.add(dayOfWeek)
        }
        model.update { it.copy(daysOfWeek = newDaysOfWeek) }

    }

    override fun onPickDateClicked() {
        model.update { it.copy(showDatePicker = true) }
    }

    override fun onDatePickerCancel() {
        model.update { it.copy(showDatePicker = false) }
    }

    override fun onDatePickerOk(date: Date?) {
        model.update { it.copy(showDatePicker = false) }
        if (date != null) {
            model.update { it.copy(endDate = date) }
        }
    }

    override fun onSaveClicked() {
        model.value.apply {
            val repeat: Repeat = RepeatRecord.Repeater()
                .every(everyAmount, everyPeriod)
                .setDaysOfWeek(
                    daysOfWeek.map { it.toCalendar() }
                )
                .apply {
                    when (endType) {
                        EndType.Date -> end(endDate)
                        EndType.Times -> end(endTimes)
                    }
                }
                .repeat()
            finish(repeat)
        }
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
