package com.murzify.meetum.feature.calendar.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.local
import com.murzify.meetum.feature.calendar.components.RecordInfoComponent
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc

@SuppressLint("ComposableNaming")
@Composable
actual fun onPhoneLongClick(model: RecordInfoComponent.Model) {
    StringDesc.Plural(MR.plurals.day, 4).local()
    val context = LocalContext.current
    val uri = "tel:${model.record}".toUri()
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = uri
    context.startActivity(intent)
}