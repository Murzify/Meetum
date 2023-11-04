package com.murzify.meetum.feature.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.murzify.meetum.core.domain.model.DateSerializer
import com.murzify.meetum.feature.calendar.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DayOfWeek
import java.util.Date

interface RepetitiveEventsComponent {
    val model: StateFlow<Model>

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

    @Serializable
    data class Model(
        val everyAmount: Int,
        val everyPeriod: Int,
        val daysOfWeek: List<DayOfWeek>,
        val showDaysOfWeek: Boolean,
        val endTimes: Int,
        @Serializable(with = DateSerializer::class)
        val endDate: Date,
        val endType: EndType,
        val showDatePicker: Boolean
    )

    @Serializable(with = EndTypeSerializer::class)
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


