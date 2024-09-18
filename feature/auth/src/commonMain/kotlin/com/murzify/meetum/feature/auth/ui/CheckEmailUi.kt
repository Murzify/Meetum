package com.murzify.meetum.feature.auth.ui

import KottieAnimation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.check_email
import com.murzify.meetum.feature.auth.components.CheckEmailComponent
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CheckEmailUi(component: CheckEmailComponent) {
    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        animation = Res.readBytes("lottie/email_verif.json").decodeToString()
    }
    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation)
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
            text = stringResource(Res.string.check_email),
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }


}