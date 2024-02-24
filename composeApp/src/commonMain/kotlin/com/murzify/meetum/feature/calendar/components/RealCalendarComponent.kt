package com.murzify.meetum.feature.calendar.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.toStateFlow
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

fun ComponentFactory.createCalendarComponent(
    componentContext: ComponentContext,
    splitScreen: Boolean,
    navigateToAddService: () -> Unit,
) : CalendarComponent = RealCalendarComponent(
    componentContext,
    navigateToAddService,
    splitScreen,
    this
)

class RealCalendarComponent(
    componentContext: ComponentContext,
    override val navigateToAddService: () -> Unit,
    override val splitScreen: Boolean,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, CalendarComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, CalendarComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.RecordsManager,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): CalendarComponent.Child = when (config) {
        is ChildConfig.RecordsManager -> CalendarComponent.Child.RecordsManager(
            componentFactory.createRecordsManagerComponent(
                componentContext,
                navigateToAddRecord = { date, record ->
                    navigation.push(ChildConfig.AddRecord(date, record))
                },
                navigateToRecordInfo = { record, date ->
                    navigation.push(ChildConfig.RecordInfo(record, date))
                }
            )
        )
        is ChildConfig.AddRecord -> CalendarComponent.Child.AddRecord(
            componentFactory.createAddRecordComponent(
                componentContext,
                config.recordTime,
                config.record,
                navigateBack = navigation::pop,
                navigateToCalendar = { navigation.replaceAll(ChildConfig.RecordsManager) },
                navigateToRepeat = { navigation.push(ChildConfig.RepetitiveEvents) },
                navigateToAddService = navigateToAddService
            )
        )
        is ChildConfig.RecordInfo -> CalendarComponent.Child.RecordInfo(
            RealRecordInfoComponent(
                componentContext,
                config.record,
                recordTime = config.recordTime,
                navigateBack = navigation::pop,
                navigateToEdit = {
                    navigation.push(
                        ChildConfig.AddRecord(
                            config.recordTime,
                            config.record
                        )
                    )
                }
            )
        )

        ChildConfig.RepetitiveEvents -> CalendarComponent.Child.RepetitiveEvents(
            RealRepetitiveEventsComponent(
                componentContext,
                navigateBack = navigation::pop,
                finish = { repeat ->
                    navigation.pop()
                    val addRecordComponent = (
                            childStack.value.active.instance as CalendarComponent.Child.AddRecord)
                    addRecordComponent.component.onRepeatReceived(repeat)
                }
            )
        )
    }

    @Serializable
    private sealed interface ChildConfig {
        @Serializable
        data object RecordsManager: ChildConfig

        @Serializable
        data class AddRecord(
            val recordTime: RecordTime,
            val record: Record? = null
        ): ChildConfig

        @Serializable
        data class RecordInfo(
            val record: Record,
            val recordTime: RecordTime,
        ): ChildConfig

        @Serializable
        data object RepetitiveEvents: ChildConfig

    }


}