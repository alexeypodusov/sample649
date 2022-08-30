package ru.alexeypodusov.sample649.base.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Colors.defaultImageBackground
    get() = DarkGray

val Colors.deleteIcon
    get() = if (isLight) DarkGray else LightGray

val Colors.secondScreenBackgroundColor
    get() = if (isLight) LightGray else DarkBlue

val Colors.bottomBarAccent
    get() = if (isLight) Purple500 else Purple200

private val DarkColors = darkColors(
    primary = Color.White,
    surface = LightBlack,
    secondary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
)
private val LightColors = lightColors(
    primary = Color.Black,
    surface = Color.White,
    secondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
)

@Composable
fun Sample649Theme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors,
        content = content
    )
}