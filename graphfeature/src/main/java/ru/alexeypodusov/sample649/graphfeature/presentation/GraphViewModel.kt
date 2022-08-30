package ru.alexeypodusov.sample649.graphfeature.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.alexeypodusov.sample649.base.presentation.BaseViewModel

class GraphViewModel: BaseViewModel<GraphState, GraphEvent>() {
    override val uiState = MutableStateFlow(
        GraphState(
            listOf(
                GraphItem(328, "Январь"),
                GraphItem(402, "Февраль"),
                GraphItem(245, "Март"),
                GraphItem(700, "Апрель"),
                GraphItem(887, "Июнь"),
                GraphItem(124, "Август")
            )
        )
    ).asStateFlow()
    override fun obtainEvent(event: GraphEvent) {}
}