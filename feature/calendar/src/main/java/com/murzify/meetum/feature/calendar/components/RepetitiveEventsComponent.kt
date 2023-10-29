package com.murzify.meetum.feature.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.murzify.meetum.feature.calendar.R
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.util.Date

interface RepetitiveEventsComponent {
    val everyAmount: StateFlow<Int>
    val everyPeriod: StateFlow<Int>
    val daysOfWeek: StateFlow<List<DayOfWeek>>
    val showDaysOfWeek: StateFlow<Boolean>
    val endTimes: StateFlow<Int>
    val endDate: StateFlow<Date>
    val endType: StateFlow<EndType>
    val showDatePicker: StateFlow<Boolean>

    fun onEveryAmountChanged(amount: String)
    fun onPeriodChanged(period: Int)
    fun onEndTimesChanged(times: String)
    fun ondEndDateChanged(date: Date)
    fun onEndTypeChanged(endType: EndType)
    fun onDayOfWeekClick(dayOfWeek: DayOfWeek)
    fun onPickDateClicked()
    fun onDatePickerCancel()
    fun onDatePickerOk(date: Date?)
    fun onSaveClicked()
    fun onBackClicked()
    sealed interface EndType {
        data object Date: EndType
        data object Times: EndType {

            @Composable
            fun getText(times: Int) = (
                stringResource(id = R.string.after) to pluralStringResource(
                    id = R.plurals.times, count = times
                )
            )
        }
    }
}
