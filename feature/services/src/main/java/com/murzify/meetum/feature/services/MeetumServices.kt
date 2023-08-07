package com.murzify.meetum.feature.services

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.murzify.meetum.core.domain.model.Service
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

val serviceExample = Service(
    "Massage",
    200.toDouble(),
    Currency.getInstance("RUB")
)


@Composable
internal fun ServicesListRoute(
    navigateToAddService: () -> Unit,
    viewModel: ServicesViewModel = hiltViewModel()
) {
    val services by viewModel.services.collectAsState()
    MeetumService(services, navigateToAddService)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
internal fun MeetumService(
    services: List<Service> = listOf(serviceExample, serviceExample),
    navigateToAddService: () -> Unit = {}
) {
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
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(services) {
                ServiceCard(it)
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
                onClick = {
                    navigateToAddService()
                }
            ) {
                Icon(
                    painter = painterResource(id = com.murzify.ui.R.drawable.round_add_24),
                    contentDescription = stringResource(id = R.string.add_service)
                )
            }
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "spec:width=700px,height=700px,dpi=440"
)
@Composable
internal fun ServiceCard(service: Service = serviceExample) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,

        ) {
            Text(text = service.name, fontSize = 20.sp)
            val format = localStyleForeignFormat(Locale.getDefault())
            format.currency = service.currency
            val price = format.format(service.price)
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = price, fontSize = 20.sp, textAlign = TextAlign.End)
            }
        }
    }
}

fun localStyleForeignFormat(locale: Locale): NumberFormat {
    val format = NumberFormat.getCurrencyInstance(locale)
    if (format is DecimalFormat) {
        // use local/default decimal symbols with original currency symbol
        val dfs = DecimalFormat().decimalFormatSymbols
        dfs.currency = format.currency
        format.decimalFormatSymbols = dfs
    }
    return format
}