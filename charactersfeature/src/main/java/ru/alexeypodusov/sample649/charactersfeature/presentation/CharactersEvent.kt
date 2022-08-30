package ru.alexeypodusov.sample649.charactersfeature.presentation

sealed class CharactersEvent {
    class ItemDeleted(val position: Int) : CharactersEvent()
    object SearchButtonClicked : CharactersEvent()
    object ToolbarBackButtonClicked : CharactersEvent()
    class SearchInput(val text: String) : CharactersEvent()
    object RetryButtonClicked : CharactersEvent()
    object OnStart : CharactersEvent()
    object OnStop : CharactersEvent()
}