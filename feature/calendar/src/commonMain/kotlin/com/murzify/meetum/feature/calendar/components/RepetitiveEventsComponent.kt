package com.murzify.meetum.feature.calendar.components

import androidx.compose.runtime.Composable
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.after_times
import com.murzify.meetum.core.ui.resources.times
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

interface RepetitiveEventsComponent {
    val model: StateFlow<Model>

    fun onEveryAmountChanged(amount: String)
    fun onPeriodChanged(period: DateTimeUnit)
    fun onEndTimesChanged(times: String)
    fun ondEndDateChanged(date: LocalDateTime)
    fun onEndTypeChanged(endType: EndType)
    fun onDayOfWeekClick(dayOfWeek: DayOfWeek)
    fun onPickDateClicked()
    fun onDatePickerCancel()
    fun onDatePickerOk(date: LocalDateTime?)
    fun onSaveClicked()
    fun onBackClicked()

    @Serializable
    data class Model(
        val everyAmount: Int,
        val everyPeriod: DateTimeUnit,
        val daysOfWeek: List<DayOfWeek>,
        val showDaysOfWeek: Boolean,
        val endTimes: Int,
        val endDate: Instant,
        val endType: EndType,
        val showDatePicker: Boolean
    )

    @Serializable(with = EndTypeSerializer::class)
    sealed interface EndType {
        data object Date: EndType

        data object Times: EndType {

            @Composable
            fun getText(times: Int) = (
                stringResource(Res.string.after_times) to pluralStringResource(
                    Res.plurals.times, times
                )
            )
        }
    }

    private object EndTypeSerializer: KSerializer<EndType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "EndType", PrimitiveKind.INT
        )

        override fun deserialize(decoder: Decoder): EndType {
            return when (decoder.decodeInt()) {
                1 -> EndType.Date
                else -> EndType.Times
            }
        }

        override fun serialize(encoder: Encoder, value: EndType) {
            encoder.encodeInt(
                when (value) {
                    EndType.Date -> 1
                    EndType.Times -> 2
                }
            )
        }

    }
}


