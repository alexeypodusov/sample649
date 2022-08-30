package ru.alexeypodusov.sample649.graphfeature.presentation.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

data class GraphItemDrawData(
    val name: String,
    var height: Dp,
    var color: Color
)
