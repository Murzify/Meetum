package com.murzify.meetum.core.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.nunito_regular
import org.jetbrains.compose.resources.Font


val meetumFont @Composable get() = Font(Res.font.nunito_regular)
val meetumFontFamily @Composable get() = FontFamily(meetumFont)

val textStyle @Composable get() = TextStyle(
    fontFamily = meetumFontFamily
)

val meetumTypography @Composable get() = Typography(
    bodyMedium = textStyle,
    bodySmall = textStyle,
    bodyLarge = textStyle
)