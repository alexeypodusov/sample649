@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package ru.alexeypodusov.sample649.charactersfeature.presentation.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersComponentProvider
import ru.alexeypodusov.sample649.charactersfeature.presentation.CharactersEvent
import ru.alexeypodusov.sample649.charactersfeature.presentation.CharactersState
import ru.alexeypodusov.sample649.charactersfeature.presentation.CharactersViewModel
import ru.alexeypodusov.sample649.charactersfeature.presentation.Character
import ru.alexeypodusov.sample649.base.theme.*
import ru.alexeypodusov.sample649.charactersfeature.R
import ru.alexeypodusov.searchabletoolbar.compose.SearchableToolbar
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CharactersScreen(
    viewModel: CharactersViewModel = viewModel(
        modelClass = CharactersViewModel::class.java,
        factory = (LocalContext.current.applicationContext as CharactersComponentProvider)
            .provideCharactersComponent()
            .viewModelFactory
    )
) {
    var toolbarHeightPx by remember { mutableStateOf(0f) }
    var toolbarOffsetHeightPx by remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    viewModel.uiState.collectAsState().value.let { state ->
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()
        val openedBarColor = MaterialTheme.colors.surface
        val backgroundColor = MaterialTheme.colors.secondScreenBackgroundColor

        DisposableEffect(state.isOpenedSearchBar) {
            systemUiController.setSystemBarsColor(
                color = if (state.isOpenedSearchBar) openedBarColor else backgroundColor,
                darkIcons = useDarkIcons
            )
            onDispose {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }
        }

        Box (
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colors.secondScreenBackgroundColor)
                .nestedScroll(nestedScrollConnection)
        ) {
            when(state) {
                is CharactersState.Loaded -> LoadedScreen(
                    state.characters,
                    viewModel,
                    (toolbarHeightPx + toolbarOffsetHeightPx).roundToInt()
                )
                is CharactersState.Loading -> LoadingScreen()
                is CharactersState.Empty -> EmptyScreen()
                is CharactersState.Error -> ErrorScreen(
                    state.errorMessage.getString(LocalContext.current),
                    viewModel
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()) }
                    .onGloballyPositioned {
                        toolbarHeightPx = it.size.height.toFloat()
                    }
            ) {
                SearchableToolbar(
                    hint = stringResource(R.string.searchbar_hint),
                    hintColor = MaterialTheme.colors.searchBarHint,
                    textFieldColor = MaterialTheme.colors.searchBarTextField,
                    backgroundColor = MaterialTheme.colors.surface,
                    iconColor = MaterialTheme.colors.searchBarIcon,
                    isOpened = state.isOpenedSearchBar,
                    onSearchClicked = {
                        viewModel.obtainEvent(CharactersEvent.SearchButtonClicked)
                    },
                    onBackClicked = {
                        viewModel.obtainEvent(CharactersEvent.ToolbarBackButtonClicked)
                    },
                    onTextChanged = {
                        viewModel.obtainEvent(CharactersEvent.SearchInput(it))
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(text: String, viewModel: CharactersViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colors.secondary
            )
            Text(
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                text = text,
                color = MaterialTheme.colors.onBackground
            )
            Button(
                onClick = {
                    viewModel.obtainEvent(CharactersEvent.RetryButtonClicked)
                },
                shape = RectangleShape
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_search_24),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colors.secondary
            )
            Text(
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.empty),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun LoadedScreen(
    characters: List<Character>,
    viewModel: CharactersViewModel,
    offset: Int
) {
    LazyColumn(modifier = Modifier.offset { IntOffset(x = 0, y = offset) }) {
        itemsIndexed(
            items = characters,
            key = { _, character ->
                character.charId
            },
        ) { index, character ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                viewModel.obtainEvent(CharactersEvent.ItemDeleted(index))
            }

            var columnWidth by remember { mutableStateOf(0) }
            var columnHeight by remember { mutableStateOf(0) }
            var iconSizeInPx by remember { mutableStateOf(0) }
            var widthIconInPx by remember { mutableStateOf(0) }
            val aspectRatio by remember { mutableStateOf(0f) }

            val progress = abs(dismissState.offset.value) / columnWidth
            val maxHeightDiff = (columnHeight / 3).toFloat() - (columnHeight / 5)
            val additionalHeightByProgress = maxHeightDiff * progress
            iconSizeInPx = (columnHeight / 5 + additionalHeightByProgress).toInt()
            widthIconInPx = (iconSizeInPx * aspectRatio).toInt()


            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dismissState,
                directions = setOf(
                    DismissDirection.EndToStart
                ),
                dismissThresholds = {
                    FractionalThreshold(0.3f)
                },
                background = {
                    if (dismissState.offset.value > -columnWidth) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ic_baseline_delete_24),
                                contentDescription = null,
                                modifier = Modifier.size(
                                    with(LocalDensity.current) { iconSizeInPx.toDp() }
                                ),
                                tint = MaterialTheme.colors.deleteIcon
                            )
                        }
                    }
                },
                dismissContent = {
                    Card(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)

                    ) {
                        Column(
                            modifier = Modifier.onGloballyPositioned {
                                columnHeight = it.size.height
                                columnWidth = it.size.width
                            }
                        ) {
                            AsyncImage(
                                model = character.imgUrl,
                                contentDescription = null,
                                modifier = Modifier.height(170.dp).fillMaxWidth()
                                    .background(MaterialTheme.colors.defaultImageBackground),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = character.name,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                                color = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = character.nickname,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    start = 20.dp,
                                    bottom = 20.dp
                                ),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            )
        }
    }
}