package ru.alexeypodusov.sample649.base.presentation

import ru.alexeypodusov.sample649.base.util.UiText

sealed class SingleAction {
    class ShowSnakbar(val text: UiText, val duration: Int) : SingleAction()
    data class ShowToast(val text: UiText, val isLenghtLong: Boolean) : SingleAction()
    open class ScreenSpecificAction: SingleAction()
}




