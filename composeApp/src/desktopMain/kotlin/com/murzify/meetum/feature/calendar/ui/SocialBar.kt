package com.murzify.meetum.feature.calendar.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.domain.model.Record
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.net.URI

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
actual fun SocialBar(record: Record) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (record.phone != null) {
            Image(
                painter = painterResource("drawable/whatsapp.xml"),
                contentDescription = null,
                modifier = Modifier.clickable {
                    openPhone(record.phone, "tg://resolve?phone=")
                }   
            )
        }

        if (record.phone != null) {
            Image(
                painter = painterResource("drawable/telegram.xml"),
                contentDescription = null,
                modifier = Modifier.clickable {
                    openPhone(record.phone, "whatsapp://send/?phone=")
                }
            )
        }
    }
}

fun openPhone(phone: String, schema: String) {
    val number = phone.replace(Regex("\\D"), "")
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        try {
            val uri = URI(schema + number)
            Desktop.getDesktop().browse(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}