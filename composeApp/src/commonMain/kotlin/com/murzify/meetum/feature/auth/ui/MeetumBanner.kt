package com.murzify.meetum.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.murzify.meetum.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ColumnScope.MeetumBanner() {
    val isImeVisible = rememberUpdatedState(WindowInsets.ime.getBottom(LocalDensity.current) > 0)
    Box() {
        this@MeetumBanner.AnimatedVisibility(
            visible = !isImeVisible.value,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Icon(
                painter = painterResource("drawable/meetum_banner.xml"),
                stringResource(MR.strings.app_title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        Spacer(modifier = Modifier.height(111.dp))
    }
}