package com.murzify.meetum.feature.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ImportContactButton(
    modifier: Modifier = Modifier,
    onClick: (name: String, phone: String) -> Unit
)