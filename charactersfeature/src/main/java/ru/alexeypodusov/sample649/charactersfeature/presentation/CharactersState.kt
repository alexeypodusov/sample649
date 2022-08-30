package ru.alexeypodusov.sample649.charactersfeature.presentation

import ru.alexeypodusov.sample649.base.util.UiText

sealed class CharactersState(open val isOpenedSearchBar: Boolean) {
    data class Loading(override val isOpenedSearchBar: Boolean) : CharactersState(isOpenedSearchBar)
    data class Loaded(
        override val isOpenedSearchBar: Boolean,
        val characters: List<Character>
    ) : CharactersState(isOpenedSearchBar)
    data class Empty(override val isOpenedSearchBar: Boolean) : CharactersState(isOpenedSearchBar)
    data class Error(override val isOpenedSearchBar: Boolean, val errorMessage: UiText) : CharactersState(isOpenedSearchBar)
}

