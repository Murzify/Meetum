package com.murzify.meetum.feature.auth.ui

import KottieAnimation
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.ui.resources.Res
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmailAnimation() {
    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        animation = Res.readBytes("files/lottie/email_verif.json").decodeToString()
    }
    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation)
    )
    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = 1
    )

    KottieAnimation(
        composition = composition,
        progress = { animationState.progress },
        modifier = Modifier.size(200.dp)
    )
}