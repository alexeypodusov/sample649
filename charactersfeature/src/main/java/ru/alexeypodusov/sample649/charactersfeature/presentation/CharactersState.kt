package ru.alexeypodusov.sample649.charactersfeature.presentation

import ru.alexeypodusov.sample649.base.util.UiText

sealed class CharactersState(open var isOpenedSearchBar: Boolean) {
    data class Loading(override var isOpenedSearchBar: Boolean) : CharactersState(isOpenedSearchBar)
    data class Loaded(
        override var isOpenedSearchBar: Boolean,
        val characters: List<Character>
    ) : CharactersState(isOpenedSearchBar)
    data class Empty(override var isOpenedSearchBar: Boolean) : CharactersState(isOpenedSearchBar)
    data class Error(override var isOpenedSearchBar: Boolean, val errorMessage: UiText) : CharactersState(isOpenedSearchBar)
}

