package com.murzify.meetum.feature.calendar.components.fake

import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class FakeRepetitiveEventsComponent : RepetitiveEventsComponent {
    override val model = MutableStateFlow(
        Model(
            everyAmount = 1,
            everyPeriod = DateTimeUnit.DAY,
            daysOfWeek = listOf(),
            showDaysOfWeek = true,
            endTimes = 3,
            endDate = Clock.System.now().plus(
                3,
                DateTimeUnit.DAY,
                TimeZone.currentSystemDefault()
            ),
            endType = RepetitiveEventsComponent.EndType.Times,
            showDatePicker = false
        )
    )

    override fun onEveryAmountChanged(amount: String) {}
    override fun onPeriodChanged(period: DateTimeUnit) {}
    override fun onEndTimesChanged(times: String) {}
    override fun ondEndDateChanged(date: LocalDateTime) {}
    override fun onEndTypeChanged(endType: RepetitiveEventsComponent.EndType) {}
    override fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {}
    override fun onPickDateClicked() {}
    override fun onDatePickerCancel() {}
    override fun onDatePickerOk(date: LocalDateTime?) {}
    override fun onSaveClicked() {}
    override fun onBackClicked() {}
}