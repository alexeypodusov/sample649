package ru.alexeypodusov.sample649.charactersfeature.presentation

import ru.alexeypodusov.sample649.base.presentation.SingleAction

class ChangeStatusBarColorAction(val color: StatusBarColor):  SingleAction.ScreenSpecificAction()

sealed class StatusBarColor {
    object WindowBackground: StatusBarColor()
    object CharactersBackground : StatusBarColor()
    object SearchBar: StatusBarColor()
}

