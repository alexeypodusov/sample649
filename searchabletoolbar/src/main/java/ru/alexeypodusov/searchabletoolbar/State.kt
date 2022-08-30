package ru.alexeypodusov.searchabletoolbar

enum class State(val index: Int) {
    BUTTON(0),
    INPUT(1);

    companion object {
        fun fromIndex(index: Int, default: State): State {
            return values().firstOrNull { it.index == index } ?: default
        }
    }
}