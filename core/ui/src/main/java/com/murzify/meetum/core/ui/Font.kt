package com.murzify.meetum.core.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.murzify.ui.R

val meetumFont = Font(R.font.nunito_regular)
val meetumFontFamily = FontFamily(meetumFont)

val textStyle = TextStyle(
    fontFamily = meetumFontFamily
)

val meetumTypography = Typography(
    bodyMedium = textStyle,
    bodySmall = textStyle,
    bodyLarge = textStyle
)