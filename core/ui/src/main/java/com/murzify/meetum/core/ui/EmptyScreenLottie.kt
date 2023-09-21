package com.murzify.meetum.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.murzify.ui.R

@Preview
@Composable
fun EmptyScreenLottie(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ghost_lottie))
    LottieAnimation(
        iterations = LottieConstants.IterateForever,
        composition = composition,
        modifier = modifier,
    )
}