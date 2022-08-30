package ru.alexeypodusov.sample649.charactersfeature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersScope
import ru.alexeypodusov.sample649.base.presentation.BaseViewModel
import ru.alexeypodusov.sample649.base.util.UiText
import ru.alexeypodusov.sample649.base.util.uiText
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepository
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CharactersViewModel constructor(
    private val charactersRepository: CharactersRepository
) : BaseViewModel<CharactersState, CharactersEvent>() {

    private val query = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 50,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply { tryEmit("") }

    private val progress = MutableStateFlow(true)
    private val characters = MutableStateFlow<List<Character>>(listOf())
    private val isOpenedSearchBar = MutableStateFlow(false)
    private val error = MutableStateFlow<UiText?>(null)

    override val uiState = combine(
        characters,
        progress,
        isOpenedSearchBar,
        error,
        transform = {
                characters,
                progress,
                isOpenedSearchBar,
                error ->
            when {
                error != null -> return@combine CharactersState.Error(
                    isOpenedSearchBar,
                    error
                )
                progress -> return@combine CharactersState.Loading(
                    isOpenedSearchBar
                )
                characters.isEmpty() -> return@combine CharactersState.Empty(
                    isOpenedSearchBar
                )
            }

            CharactersState.Loaded(
                isOpenedSearchBar,
                characters
            )
        }
    ).stateIn(viewModelScope, SharingStarted.Lazily,
        CharactersState.Loading(false)
    )

    init {
        viewModelScope.launch {
            query
                .debounce(200)
                .mapLatest { loadCharacters(it) }
                .map { characterResponses ->
                    characterResponses.map {
                        CharacterMapper.transform(
                            it
                        )
                    }
                }
                .collect {
                    progress.value = false
                    characters.value = it
                }
        }
    }

    private fun onItemDeleted(position: Int) {
        characters.value = characters.value
            .toMutableList()
            .apply {
                removeAt(position)
            }
    }

    private fun onRetryButtonClicked() {
        viewModelScope.launch {
            with(this@CharactersViewModel.query.take(1).last()) {
                this@CharactersViewModel.query.emit(this)
            }
        }
    }

    private suspend fun loadCharacters(query: String): List<CharacterResponse> {
        error.value?.let {
            progress.value = true
            error.value = null
        }
        return try {
            charactersRepository.loadCharacters(query)
        } catch (e: Exception) {
            error.value = e.uiText()
            emptyList()
        }
    }

    override fun obtainEvent(event: CharactersEvent) {
        when (event) {
            is CharactersEvent.ItemDeleted -> onItemDeleted(event.position)
            CharactersEvent.RetryButtonClicked -> onRetryButtonClicked()
            is CharactersEvent.SearchInput -> this.query.tryEmit(event.text)
            CharactersEvent.SearchButtonClicked -> isOpenedSearchBar.value = true
            CharactersEvent.ToolbarBackButtonClicked -> isOpenedSearchBar.value = false
            CharactersEvent.OnStart -> onStart()
            CharactersEvent.OnStop ->
                _singleActions.tryEmit(ChangeStatusBarColorAction(StatusBarColor.WindowBackground))
        }
    }

    private fun onStart() {
        when(isOpenedSearchBar.value) {
            true -> StatusBarColor.SearchBar
            false -> StatusBarColor.CharactersBackground
        }.run {
            _singleActions.tryEmit(
                ChangeStatusBarColorAction(this)
            )
        }
    }

    @CharactersScope
    class Factory @Inject constructor(
        private val charactersRepository: CharactersRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CharactersViewModel(charactersRepository) as T
        }
    }
}