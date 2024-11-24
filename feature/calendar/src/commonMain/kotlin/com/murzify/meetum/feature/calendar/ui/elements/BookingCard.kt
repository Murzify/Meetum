package com.murzify.meetum.feature.calendar.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.domain.model.RecordTime
import kotlinx.datetime.toJavaInstant
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingCard(
    record: Record,
    recordTime: RecordTime,
    onClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!record.clientName.isNullOrEmpty()) {
                Text(
                    text = record.clientName!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(text = record.service.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date.from(recordTime.time.toJavaInstant())
        Text(
            text = sdf.format(date),
            modifier = Modifier.padding(start = 2.dp, end = 16.dp)
        )
    }
}