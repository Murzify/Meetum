package com.murzify.meetum.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
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
                        painter = painterResource("drawable/round_add_24.xml"),
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