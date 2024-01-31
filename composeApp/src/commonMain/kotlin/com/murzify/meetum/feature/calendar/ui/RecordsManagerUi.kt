package com.murzify.meetum.feature.calendar.ui

import androidx.compose.runtime.Composable
import com.murzify.meetum.feature.calendar.components.RecordsManagerComponent

@Composable
expect fun RecordsManagerUi(
    component: RecordsManagerComponent
)