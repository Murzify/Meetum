package com.murzify.meetum.feature.calendar.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.domain.model.Record
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.sms
import com.murzify.meetum.core.ui.resources.telegram
import com.murzify.meetum.core.ui.resources.whatsapp
import org.jetbrains.compose.resources.painterResource


@Composable
actual fun SocialBar(record: Record) {
    val packageManager = LocalContext.current.packageManager
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (record.phone != null && isPackageInstalled("com.whatsapp", packageManager)) {
            Image(
                painter = painterResource(Res.drawable.whatsapp),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.openWA(record.phone!!)
                }
            )
        }
        if (record.phone != null && context.checkScheme("tg://resolve")) {
            Image(
                painter = painterResource(Res.drawable.telegram),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.openTg(record.phone!!)
                }
            )
        }

        if (record.phone != null) {
            Image(
                painter = painterResource(Res.drawable.sms),
                contentDescription = null,
                modifier = Modifier.clickable {
                    context.sendSms(record.phone!!)
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
    val waIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
    waIntent.setPackage("com.whatsapp")
    startActivity(waIntent)
}

private fun Context.checkScheme(scheme: String): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
    val activities = packageManager.queryIntentActivities(intent, 0)
    return activities.isNotEmpty()
}

private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
    try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
}