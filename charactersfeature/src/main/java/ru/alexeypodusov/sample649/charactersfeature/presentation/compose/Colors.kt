package ru.alexeypodusov.sample649.charactersfeature.presentation.compose

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import ru.alexeypodusov.sample649.base.theme.Grey575757
import ru.alexeypodusov.sample649.base.theme.LightGray

internal val Colors.searchBarHint
    get() = if (isLight) Grey575757 else LightGray

internal val Colors.searchBarTextField
    get() = if (isLight) Color.Black else Color.White

internal val Colors.searchBarIcon
    get() = if (isLight) Grey575757 else LightGray
