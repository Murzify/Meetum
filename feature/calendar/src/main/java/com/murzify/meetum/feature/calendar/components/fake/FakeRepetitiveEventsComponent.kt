package com.murzify.meetum.feature.calendar.components.fake

import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent
import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent.Model
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

class FakeRepetitiveEventsComponent : RepetitiveEventsComponent {
    override val model = MutableStateFlow(
        Model(
            everyAmount = 1,
            everyPeriod = Calendar.DATE,
            daysOfWeek = listOf(),
            showDaysOfWeek = true,
            endTimes = 3,
            endDate = Calendar.getInstance().apply {
                time = Date()
                add(Calendar.DATE, 3)
            }.time,
            endType = RepetitiveEventsComponent.EndType.Times,
            showDatePicker = false
        )
    )

    override fun onEveryAmountChanged(amount: String) {}
    override fun onPeriodChanged(period: Int) {}
    override fun onEndTimesChanged(times: String) {}
    override fun ondEndDateChanged(date: Date) {}
    override fun onEndTypeChanged(endType: RepetitiveEventsComponent.EndType) {}
    override fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {}
    override fun onPickDateClicked() {}
    override fun onDatePickerCancel() {}
    override fun onDatePickerOk(date: Date?) {}
    override fun onSaveClicked() {}
    override fun onBackClicked() {}
}