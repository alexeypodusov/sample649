package ru.alexeypodusov.searchabletoolbar.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val MAX_ROUND_BACKGROUND_SIZE = 30.dp
private val MAX_HORIZONTAL_PADDING = 16.dp
private val MAX_VERTICAL_PADDING = 20.dp
private const val DURATION = 100

@Composable
internal fun BarBackground(isOpened: Boolean, color: Color) {
    var horizontalPadding by remember { mutableStateOf(if (isOpened) 0.dp else MAX_HORIZONTAL_PADDING)  }
    val horizontalPaddingAnimState by animateDpAsState(
        targetValue = horizontalPadding,
        tween(durationMillis = DURATION, easing = LinearOutSlowInEasing)
    )

    var verticalPadding by remember { mutableStateOf(if (isOpened) 0.dp else MAX_VERTICAL_PADDING)  }
    val verticalPaddingAnimState by animateDpAsState(
        targetValue = verticalPadding,
        tween(durationMillis = DURATION, easing = LinearOutSlowInEasing)
    )

    var roundSize by remember { mutableStateOf(if (isOpened) 0.dp else MAX_ROUND_BACKGROUND_SIZE) }
    val roundSizeAnimState by animateDpAsState(
        targetValue = roundSize,
        tween(durationMillis = DURATION, easing = LinearOutSlowInEasing)
    )

    when(isOpened) {
        true -> {
            roundSize = 0.dp
            verticalPadding = 0.dp
            horizontalPadding = 0.dp
        }
        false -> {
            roundSize = MAX_ROUND_BACKGROUND_SIZE
            verticalPadding = MAX_VERTICAL_PADDING
            horizontalPadding = MAX_HORIZONTAL_PADDING
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = horizontalPaddingAnimState, vertical = verticalPaddingAnimState)
            .clip(shape = RoundedCornerShape(roundSizeAnimState))
            .background(color)
    )
}