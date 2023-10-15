package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.core.domain.model.Record
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Date

fun ComponentFactory.createCalendarComponent(
    componentContext: ComponentContext
) : CalendarComponent = RealCalendarComponent(componentContext, this)

class RealCalendarComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, CalendarComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, CalendarComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.RecordsManager,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): CalendarComponent.Child = when (config) {
        is ChildConfig.RecordsManager -> CalendarComponent.Child.RecordsManager(
            componentFactory.createRecordsManagerComponent(componentContext) {
                navigation.push(ChildConfig.AddRecord(it))
            }
        )
        is ChildConfig.AddRecord -> CalendarComponent.Child.AddRecord(
            componentFactory.createAddRecordComponent(
                componentContext,
                config.date,
                config.record
            )
        )
        is ChildConfig.RecordInfo -> CalendarComponent.Child.RecordInfo(
            componentFactory.createRecordInfoComponent(
                componentContext,
                MutableStateFlow(config.record)
            ) {
                navigation.push(
                    ChildConfig.AddRecord(
                        config.record.time.first(),
                        config.record
                    )
                )
            }
        )
    }

    private sealed interface ChildConfig: Parcelable {
        @Parcelize
        data object RecordsManager: ChildConfig

        @Parcelize
        data class AddRecord(val date: Date, val record: @RawValue Record? = null): ChildConfig

        @Parcelize
        data class RecordInfo(val record: @RawValue Record): ChildConfig

    }


}