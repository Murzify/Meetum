package com.murzify.meetum.root.navigation

import com.murzify.meetum.MR
import dev.icerock.moko.resources.StringResource
import meetum.composeapp.generated.resources.Res
import meetum.composeapp.generated.resources.round_calendar_today_24
import meetum.composeapp.generated.resources.round_handshake_24
import org.jetbrains.compose.resources.DrawableResource

sealed class Screen(val stringId: StringResource, val iconRes: DrawableResource) {
    data object Calendar : Screen(
        MR.strings.calendar_label,
        Res.drawable.round_calendar_today_24
    )

    data object Services : Screen(
        MR.strings.services_label,
        Res.drawable.round_handshake_24
    )
}
