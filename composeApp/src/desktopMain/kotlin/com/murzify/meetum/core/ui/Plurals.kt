package com.murzify.meetum.core.ui

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.desc.PluralStringDesc
import java.util.Locale

@Composable
actual fun PluralStringDesc.local(): String {
    return pluralsRes.localized(Locale.getDefault(), number)
}