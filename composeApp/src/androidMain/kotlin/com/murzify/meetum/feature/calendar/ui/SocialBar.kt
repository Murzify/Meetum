package com.murzify.meetum.feature.calendar.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.domain.model.Record
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun SocialBar(record: Record) {
    val packageManager = LocalContext.current.packageManager
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (record.phone != null && isPackageInstalled("com.whatsapp", packageManager)) {
            Image(
                painter = painterResource("drawable/whatsapp.xml"),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.openWA(record.phone)
                }
            )
        }

        if (record.phone != null && isPackageInstalled("org.telegram.messenger", packageManager)) {
            Image(
                painter = painterResource("drawable/telegram.xml"),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.openTg(record.phone)
                }
            )
        }

        if (record.phone != null) {
            Image(
                painter = painterResource("drawable/sms.xml"),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.sendSms(record.phone)
                }
            )
        }
    }
}

private fun Context.sendSms(phone: String) {
    val number = phone.replace(Regex("[^\\d+]"), "")
    val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
    startActivity(smsIntent)
}

private fun Context.openTg(phone: String) {
    val number = phone.replace(Regex("[^\\d+]"), "")
    val tgIntent = Intent(Intent.ACTION_VIEW)
    tgIntent.setData(Uri.parse("tg://resolve?domain=$number"))
    startActivity(tgIntent)
}

private fun Context.openWA(phone: String) {
    val number = phone.replace(Regex("^\\d"), "")
    Log.d("whatsapp", number)
    val waIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
    waIntent.setPackage("com.whatsapp")
    startActivity(waIntent)
}

private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
    try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
}