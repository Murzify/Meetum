package com.murzify.meetum.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Screen(@StringRes val stringId: Int, @DrawableRes val iconId: Int) {
    data object Calendar : Screen(
        com.murzify.meetum.feature.calendar.R.string.calendar_label,
        com.murzify.meetum.feature.calendar.R.drawable.round_calendar_today_24
    )

    data object Services : Screen(
        com.murzify.meetum.feature.services.R.string.services_label,
        com.murzify.ui.R.drawable.round_handshake_24
    )
}
