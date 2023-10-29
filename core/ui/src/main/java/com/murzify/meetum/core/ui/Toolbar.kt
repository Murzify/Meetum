package com.murzify.meetum.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.murzify.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    titleText: String,
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
                title = {
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(modifier = Modifier
                        .padding(8.dp),
                        onClick = onBackClicked
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = stringResource(id = R.string.back_button)
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