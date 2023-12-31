package com.murzify.meetum.core.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun isMediumWindow(): Boolean {
    val windowSizeClass = calculateWindowSizeClass()
    val width = windowSizeClass.widthSizeClass
    val height = windowSizeClass.heightSizeClass
    return width >= WindowWidthSizeClass.Medium && height >= WindowHeightSizeClass.Medium
}