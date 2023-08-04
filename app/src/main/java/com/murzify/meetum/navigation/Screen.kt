package com.murzify.meetum.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.murzify.meetum.feature.calendar.navigation.calendarNavigationRoute
import com.murzify.meetum.feature.services.navigation.servicesNavigationRoute

sealed class Screen(val route: String, @StringRes val stringId: Int, @DrawableRes val iconId: Int) {
    object Calendar : Screen(
        calendarNavigationRoute,
        com.murzify.meetum.feature.calendar.R.string.calendar_label,
        com.murzify.meetum.feature.calendar.R.drawable.round_calendar_today_24
    )

    object Services : Screen(
        servicesNavigationRoute,
        com.murzify.meetum.feature.services.R.string.services_label,
        com.murzify.meetum.feature.services.R.drawable.round_handshake_24
    )
}
