package com.murzify.meetum.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import meetum.composeapp.generated.resources.Res
import meetum.composeapp.generated.resources.back_button
import meetum.composeapp.generated.resources.round_arrow_back_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun Toolbar(
    title: @Composable () -> Unit,
    onBackClicked: () -> Unit,
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = fab,
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            CenterAlignedTopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = onBackClicked
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.round_arrow_back_24),
                            contentDescription = stringResource(Res.string.back_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        content(it)
    }
}