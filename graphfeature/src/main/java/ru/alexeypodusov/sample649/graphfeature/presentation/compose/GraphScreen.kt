package ru.alexeypodusov.sample649.graphfeature.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphViewModel

@Composable
fun GraphScreen(viewModel: GraphViewModel = viewModel()) {
    viewModel.uiState.collectAsState().value.let {
        Box(
            modifier = Modifier.background(MaterialTheme.colors.BackroundColor)
                .padding(
                    horizontal = 16.dp,
                    vertical = 32.dp
                )
        ) {
            Graph(
                graphItems = it.items,
                minItemColor = MaterialTheme.colors.MinItemColor,
                maxItemColor = MaterialTheme.colors.MaxItemColor,
                textColor = MaterialTheme.colors.TextColor,
                lineColor = MaterialTheme.colors.LineColor
            )
        }
    }
}
