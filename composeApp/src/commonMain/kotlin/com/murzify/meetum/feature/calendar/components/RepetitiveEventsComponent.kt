package com.murzify.meetum.feature.calendar.components

import androidx.compose.runtime.Composable
import com.murzify.meetum.MR
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc
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
                stringResource(MR.strings.after_times) to StringDesc.Plural(
                    MR.plurals.times, times
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


