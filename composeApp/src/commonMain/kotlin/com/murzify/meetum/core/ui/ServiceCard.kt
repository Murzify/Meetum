package com.murzify.meetum.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.MR
import com.murzify.meetum.core.domain.model.Service
import dev.icerock.moko.resources.compose.stringResource
import meetum.composeapp.generated.resources.Res
import meetum.composeapp.generated.resources.round_add_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

val serviceExample = Service(
    "Massage",
    200.toDouble(),
    Currency.getInstance("RUB")
)

@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    service: Service = serviceExample,
    onClick: (service: Service) -> Unit = {}
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp),
        border = border
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick(service)
                }
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,

                ) {
                Text(
                    text = service.name,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                val format = priceFormat(Locale.getDefault(), service.currency)
                if (service.price.rem(1.0) == 0.0) format.maximumFractionDigits = 0
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
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AddServiceCard(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ) {

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
                {
                    Icon(
                        painter = painterResource(Res.drawable.round_add_24),
                        contentDescription = stringResource(MR.strings.add_new_service),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = stringResource(MR.strings.add_new_service),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
            }
        }
    }
}

fun priceFormat(locale: Locale, currency: Currency): NumberFormat {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.currency = currency
    if (format is DecimalFormat) {
        val dfs = DecimalFormat().decimalFormatSymbols
        dfs.currency = format.currency
        format.decimalFormatSymbols = dfs
    }
    return format
}