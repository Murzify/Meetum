package com.murzify.meetum.root.navigation

import com.murzify.meetum.MR
import dev.icerock.moko.resources.StringResource

sealed class Screen(val stringId: StringResource, val iconPath: String) {
    data object Calendar : Screen(
        MR.strings.calendar_label,
        "drawable/round_calendar_today_24.xml"
    )

    data object Services : Screen(
        MR.strings.services_label,
        "drawable/round_handshake_24.xml"
    )
}
