package com.murzify.meetum.feature.calendar.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent
import meetum.composeapp.generated.resources.Res
import meetum.composeapp.generated.resources.day
import org.jetbrains.compose.resources.pluralStringResource

@SuppressLint("ComposableNaming")
@Composable
actual fun onPhoneLongClick(model: RecordInfoComponent.Model) {
    pluralStringResource(Res.plurals.day, 4)
    val context = LocalContext.current
    val uri = "tel:${model.record}".toUri()
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = uri
    context.startActivity(intent)
}