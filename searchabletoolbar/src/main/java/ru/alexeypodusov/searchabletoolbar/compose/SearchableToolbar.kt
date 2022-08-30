package ru.alexeypodusov.searchabletoolbar.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexeypodusov.searchabletoolbar.R

typealias OnButtonClicked = () -> Unit
typealias OnTextChangedListener = (String) -> Unit

@Composable
fun SearchableToolbar(
    hint: String = "",
    hintColor: Color = Color.Gray,
    textFieldColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    iconColor: Color = Color.Gray,
    isOpened: Boolean = true,
    onSearchClicked: OnButtonClicked = {},
    onBackClicked: OnButtonClicked = {},
    onTextChanged: OnTextChangedListener = {}
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isOpened) {
        if (isOpened) {
            focusRequester.requestFocus()
        }
    }

    BackHandler(isOpened) {
        if (isOpened) {
            onBackClicked.invoke()
        }
    }

    var searchText by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        BarBackground(isOpened, backgroundColor)
        Row(
            modifier = Modifier.fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    if (!isOpened) {
                        onSearchClicked.invoke()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                BackIcon(isOpened, iconColor, onBackClicked, onTextChanged)
                SearchIcon(isOpened, iconColor)
            }

            when (isOpened) {
                true -> {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            onTextChanged.invoke(it)
                        },
                        textStyle = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(end = 16.dp, top = 4.dp)
                            .offset(x = (-11).dp, y = (-1).dp)
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                text = hint,
                                fontSize = 16.sp,
                                color = hintColor
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = textFieldColor,
                            disabledTextColor = Color.Transparent,
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        maxLines = 1,
                    )
                }
                false -> {
                    searchText = ""
                    Text(
                        text = hint,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 5.dp, end = 16.dp),
                        color = hintColor
                    )
                }
            }
        }
    }
}

@Composable
private fun BackIcon(
    isOpened: Boolean,
    color: Color,
    onBackClicked: OnButtonClicked,
    onTextChanged: OnTextChangedListener
) {
    var rotate by remember { mutableStateOf(if (isOpened) 0f else -180f) }
    var alpha by remember { mutableStateOf(if (isOpened) 1f else 0f) }
    var xOffset by remember { mutableStateOf(if (isOpened) 0.dp else 16.dp) }
    var isVisible by remember { mutableStateOf(isOpened) }

    val rotateAnimState by animateFloatAsState(
        targetValue = rotate,
        tween(durationMillis = 200, easing = LinearEasing)
    )

    val alphaAnimState by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        finishedListener = {
            isVisible = it > 0.01f
        }
    )

    val xOffsetAnimState by animateDpAsState(
        targetValue = xOffset,
        tween(durationMillis = 200, easing = LinearEasing)
    )

    when (isOpened) {
        true -> {
            rotate = 0f
            alpha = 1f
            xOffset = 0.dp
            isVisible = true
        }
        false -> {
            rotate = -180f
            alpha = 0f
            xOffset = 16.dp
        }
    }
    if (isVisible) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(top = 5.dp, start = 12.dp)
                .size(30.dp)
                .offset(x = xOffsetAnimState)
                .rotate(rotateAnimState)
                .alpha(alphaAnimState)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = isOpened
                ) {
                    onBackClicked.invoke()
                    onTextChanged.invoke("")
                }
        )
    }
}

@Composable
private fun SearchIcon(isOpened: Boolean, color: Color) {
    var alpha by remember { mutableStateOf(if (isOpened) 0f else 1f) }

    val alphaAnimState by animateFloatAsState(
        targetValue = alpha,
        tween(durationMillis = 200, easing = LinearEasing)
    )

    alpha = when (isOpened) {
        true -> {
            0f
        }
        false -> {
            1f
        }
    }

    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_search_24),
        contentDescription = null,
        tint = color,
        modifier = Modifier.padding(top = 5.dp, start = 28.dp)
            .size(30.dp)
            .alpha(if (isOpened) 0f else alphaAnimState)
    )
}