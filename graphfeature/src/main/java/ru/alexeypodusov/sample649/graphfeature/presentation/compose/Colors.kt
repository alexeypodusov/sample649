package ru.alexeypodusov.sample649.graphfeature.presentation.compose

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

internal val Colors.BackroundColor
    get() = if (isLight) Color.White else Color.Black

internal val Colors.MinItemColor
    get() = if (isLight) Color(0xFF7CD9E1) else Color(0xFF7CD9E1)

internal val Colors.MaxItemColor
    get() = if (isLight) Color(0xFF3D7DB3) else Color(0xFF3D7DB3)

internal val Colors.TextColor
    get() = if (isLight) Color.Black else Color.White

internal val Colors.LineColor
    get() = Color.LightGray