package com.murzify.meetum.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun LoadingButton(
    onClick: () -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val emptyOnClick = {}
    Button(
        onClick = if (loading) emptyOnClick else onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        if (loading) {
            LoadingIndicator(indicatorSpacing = 3.dp)
        } else {
            content()
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    indicatorSpacing: Dp,
) {
    val indicatorSize = 5
    val animatedValues = List(3) { index ->
        var animatedValue by remember(key1 = Unit) { mutableStateOf(0f) }
        LaunchedEffect(key1 = Unit) {

            animate(
                initialValue = indicatorSize / 2f,
                targetValue = -indicatorSize / 2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(100 * index),
                ),
            ) { value, _ -> animatedValue = value }
        }
        animatedValue
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        animatedValues.forEach { animatedValue ->
            LoadingDot(
                modifier = Modifier
                    .padding(horizontal = indicatorSpacing)
                    .width(indicatorSize.dp)
                    .aspectRatio(1f)
                    .offset(y = animatedValue.dp),
                color = color,
            )
        }
    }
}


@Composable
private fun LoadingDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = color)
    )
}