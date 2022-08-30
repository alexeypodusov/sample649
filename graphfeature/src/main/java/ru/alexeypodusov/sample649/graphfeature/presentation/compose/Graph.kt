@file:OptIn(ExperimentalFoundationApi::class)

package ru.alexeypodusov.sample649.graphfeature.presentation.compose

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphItem
import ru.alexeypodusov.sample649.graphfeature.util.CalculateUtils

private val ITEM_FONT_SIZE = 12.sp
private val ITEM_HORIZONTAL_PADDING = 8.dp
private val ROW_FONT_SIZE = 16.sp
private const val ANIMATION_DURATION = 1000

@Composable
fun Graph(
    graphItems: List<GraphItem>,
    minItemColor: Color = Color.Gray,
    maxItemColor: Color = Color.Blue,
    textColor: Color = Color.Black,
    lineColor: Color = Color.Gray
) {
    val maxValue = graphItems.maxBy { it.value }.value
    val rowHeaders = CalculateUtils.rowValues(maxValue).toMutableList()
    rowHeaders.reverse()
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        RowHeaders(rowHeaders, textColor)
        Box(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            var linesFieldWidth by remember { mutableStateOf(0) }
            var linesFieldHeight by remember { mutableStateOf(0) }
            Lines(rowHeaders, lineColor) { width, height ->
                linesFieldWidth = width
                linesFieldHeight = height
            }
            Items(
                graphItems = graphItems,
                maxHeaderValue = rowHeaders.first().toInt(),
                linesFieldWidth = linesFieldWidth,
                linesFieldHeight = linesFieldHeight,
                minItemColor = minItemColor,
                maxItemColor = maxItemColor,
                textColor = textColor,
            )
        }
    }
}

@Composable
fun RowHeaders(headers: List<String>, textColor: Color) {
    Column(
        modifier = Modifier.fillMaxHeight()
            .padding(
                end = 16.dp,
                bottom = 18.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End
    ) {
        headers.forEach {
            Text(
                text = it,
                fontSize = ROW_FONT_SIZE,
                color = textColor
            )
        }
    }
}

@Composable
fun Lines(
    headers: List<String>,
    lineColor: Color,
    onSizeChanged: (Int, Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 26.dp)
            .onGloballyPositioned {
                onSizeChanged(it.size.width, it.size.height)
            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(headers.size) {
            Divider(color = lineColor, thickness = 1.dp)
        }
    }
}

@Composable
fun Items(
    graphItems: List<GraphItem>,
    maxHeaderValue: Int,
    linesFieldWidth: Int,
    linesFieldHeight: Int,
    minItemColor: Color,
    maxItemColor: Color,
    textColor: Color
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {

        var scrollEnabled by remember { mutableStateOf(false) }

        val itemWidth =
            with(LocalDensity.current) { (linesFieldWidth / 7).toDp() }
        val ratio = with(LocalDensity.current) { linesFieldHeight.toDp() / maxHeaderValue }

        scrollEnabled = with(LocalDensity.current) {
            (itemWidth + ITEM_HORIZONTAL_PADDING * 2) * graphItems.size > linesFieldWidth.toDp()
        }

        val drawItems = graphItems.mapIndexed { i, item ->
            GraphItemDrawData(
                name = item.name,
                height = item.value * ratio,
                color = lerp(
                    minItemColor,
                    maxItemColor,
                    100 / graphItems.size.toFloat() / 100 * i
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxSize()
                .horizontalScroll(
                    state = rememberScrollState(),
                    enabled = scrollEnabled
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom,
        ) {

            drawItems.forEach {
                var heightState by remember { mutableStateOf(0.dp) }
                val heightAnimateState by animateDpAsState(
                    targetValue = heightState,
                    tween(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing)
                )
                heightState = it.height

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = ITEM_HORIZONTAL_PADDING)
                            .background(it.color)
                            .width(itemWidth)
                            .height(heightAnimateState)
                    )
                    Box(
                        modifier = Modifier.height(26.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = it.name,
                            fontSize = ITEM_FONT_SIZE,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            color = textColor
                        )
                    }
                }
            }
        }
    }

}

