package com.murzify.meetum.core.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.murzify.meetum.MR
import dev.icerock.moko.resources.compose.asFont


val meetumFont @Composable get() = MR.fonts.nunito_regular.nunito_regular.asFont()
val meetumFontFamily @Composable get() = FontFamily(meetumFont!!)

val textStyle @Composable get() = TextStyle(
    fontFamily = meetumFontFamily
)

val meetumTypography @Composable get() = Typography(
    bodyMedium = textStyle,
    bodySmall = textStyle,
    bodyLarge = textStyle
)