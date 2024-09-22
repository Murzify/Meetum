package com.murzify.meetum.core.ui

import android.view.KeyEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent

actual fun Modifier.moveFocusDown(focusManager: FocusManager) = onPreviewKeyEvent { event ->
    if (event.key == Key.Tab && event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN){
        focusManager.moveFocus(FocusDirection.Down)
        true
    } else {
        false
    }
}