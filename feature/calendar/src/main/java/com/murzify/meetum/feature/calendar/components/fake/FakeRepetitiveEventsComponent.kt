package com.murzify.meetum.feature.calendar.components.fake

import com.murzify.meetum.feature.calendar.components.RepetitiveEventsComponent
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date

class FakeRepetitiveEventsComponent : RepetitiveEventsComponent {
    override val everyAmount = MutableStateFlow(1)
    override val everyPeriod = MutableStateFlow(Calendar.WEEK_OF_MONTH)
    override val daysOfWeek: MutableStateFlow<List<DayOfWeek>> = MutableStateFlow(emptyList())
    override val showDaysOfWeek = MutableStateFlow(true)
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