package com.murzify.meetum.feature.services.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.ui.EmptyScreenLottie
import com.murzify.meetum.core.ui.ServiceCard
import com.murzify.meetum.feature.services.R
import com.murzify.meetum.feature.services.components.ServicesListComponent

@Composable
internal fun ServicesListUi(
    component: ServicesListComponent
) {
    val showGhostLottie by component.showGhostLottie.collectAsState()
    val services by component.services.collectAsState()

    if (showGhostLottie) {
        EmptyScreenLottie()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        val gridState = rememberLazyGridState()
        val fabVisibility = remember {
            derivedStateOf {
                gridState.firstVisibleItemIndex == 0
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize()
        ) {

            items(services) {
                ServiceCard(
                    service = it,
                    onClick = component::onServiceClick
                )
            }
        }
        val density = LocalDensity.current
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
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                onClick = component::onAddServiceClick
            ) {
                Icon(
                    painter = painterResource(id = com.murzify.ui.R.drawable.round_add_24),
                    contentDescription = stringResource(id = R.string.add_service)
                )
            }
        }
    }

}