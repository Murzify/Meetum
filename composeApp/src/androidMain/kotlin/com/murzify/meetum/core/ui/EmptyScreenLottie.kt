package com.murzify.meetum.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kizitonwose.calendar.core.daysOfWeek
import com.murzify.meetum.R

@Composable
actual fun EmptyScreenLottie(modifier: Modifier) {
    daysOfWeek()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ghost_lottie))
    LottieAnimation(
        iterations = LottieConstants.IterateForever,
        composition = composition,
        modifier = modifier,
    )
}