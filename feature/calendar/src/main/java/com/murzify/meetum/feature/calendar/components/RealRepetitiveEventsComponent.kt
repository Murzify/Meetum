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
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

class RealRepetitiveEventsComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
    private val finish: (result: Repeat) -> Unit
) : RepetitiveEventsComponent, ComponentContext by componentContext {

    override val model = MutableStateFlow(
        restore(Model.serializer()) ?: Model(
            everyAmount = 1,
            everyPeriod = DateTimeUnit.DAY,
            daysOfWeek = listOf(),
            showDaysOfWeek = false,
            endTimes = 3,
            endDate = Clock.System.now().plus(
                3,
                DateTimeUnit.DAY,
                TimeZone.currentSystemDefault()
            ),
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

    override fun onPeriodChanged(period: DateTimeUnit) {
        val allowedPeriods = listOf(
            DateTimeUnit.DAY,
            DateTimeUnit.WEEK,
            DateTimeUnit.MONTH,
            DateTimeUnit.YEAR
        )
        if (period !in allowedPeriods) {
            throw IllegalStateException()
        }
        model.update {
            it.copy(
                showDaysOfWeek = period == DateTimeUnit.WEEK,
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

    override fun ondEndDateChanged(date: LocalDateTime) {
        val localDate = date.date
        val localTime = LocalTime(23, 59, 59, 0)
        model.update {
            it.copy(
                endDate = LocalDateTime(localDate, localTime)
                    .toInstant(TimeZone.currentSystemDefault())
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

    override fun onDatePickerOk(date: LocalDateTime?) {
        model.update { it.copy(showDatePicker = false) }
        if (date != null) {
            model.update { it.copy(endDate = date.toInstant(TimeZone.currentSystemDefault())) }
        }
    }

    override fun onSaveClicked() {
        model.value.apply {
            val repeat: Repeat = RepeatRecord.Repeater()
                .every(everyAmount, everyPeriod)
                .setDaysOfWeek(
                    daysOfWeek
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
 }
