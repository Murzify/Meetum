package com.murzify.meetum.feature.auth.ui

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animateKottieCompositionAsState
import com.murzify.meetum.MR
import com.murzify.meetum.feature.auth.components.CheckEmailComponent
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import rememberKottieComposition

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CheckEmailUi(component: CheckEmailComponent) {
    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(resource("lottie/email_verif.json"))
    )
    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = 1
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        KottieAnimation(
            composition = composition,
            progress = { animationState.progress },
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = stringResource(MR.strings.check_email),
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }


}