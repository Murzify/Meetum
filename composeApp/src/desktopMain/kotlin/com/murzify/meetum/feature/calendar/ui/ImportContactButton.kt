package com.murzify.meetum.feature.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ImportContactButton(
    modifier: Modifier,
    onClick: (name: String, phone: String) -> Unit,
) {
    // No button on the desktop
}