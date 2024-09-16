package com.murzify.meetum.root.navigation

import com.murzify.meetum.core.ui.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class Screen(val stringId: StringResource, val iconRes: DrawableResource) {
    data object Calendar : Screen(
        Res.string.calendar_label,
        Res.drawable.round_calendar_today_24
    )

    data object Services : Screen(
        Res.string.services_label,
        Res.drawable.round_handshake_24
    )
}
