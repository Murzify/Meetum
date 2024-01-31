package com.murzify.meetum.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.desc.PluralStringDesc

@Composable
actual fun PluralStringDesc.local(): String {
    return toString(LocalContext.current)
}