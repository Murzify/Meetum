package com.murzify.meetum.feature.services.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.EmptyScreenLottie
import com.murzify.meetum.core.ui.ServiceCard
import com.murzify.meetum.feature.services.components.ServicesListComponent
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun ServicesListUi(
    component: ServicesListComponent
) {
    val showGhostLottie by component.showGhostLottie.collectAsState()
    val services by component.services.collectAsState()

    val gridState = rememberLazyGridState()
    val fabVisibility = remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }
    val density = LocalDensity.current
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisibility.value,
                enter = slideInVertically {
                    with(density) { 40.dp.roundToPx() }
                } + fadeIn(),
                exit = fadeOut(
                    animationSpec = keyframes {
                        this.durationMillis = 120
                    }
                )
            ) {
                FloatingActionButton(
                    onClick = component::onAddServiceClick,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource("drawable/round_add_24.xml"),
                        contentDescription = stringResource(MR.strings.add_service)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (showGhostLottie) {
            EmptyScreenLottie()
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(170.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            items(services) {
                ServiceCard(
                    service = it,
                    onClick = component::onServiceClick,
                )
            }
        }
    }

}