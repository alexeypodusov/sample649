package ru.alexeypodusov.sample649.charactersfeature

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.*
import ru.alexeypodusov.sample649.charactersfeature.presentation.*
import ru.alexeypodusov.sample649.base.util.UiText
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepository
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

@OptIn(ExperimentalCoroutinesApi::class)
internal class CharactersViewModelTest {
    private val charactersRepository = mock<CharactersRepository>()

    private lateinit var charactersViewModel: CharactersViewModel

    @BeforeEach
    fun setup() {
        charactersViewModel =
            CharactersViewModel(charactersRepository)
    }

    @Test
    fun `When fragment subscribed to uiState it should return loading and loaded states`() = runTest {
        Mockito.`when`(charactersRepository.loadCharacters(anyString())).thenReturn(
            createTestCharactersResponses()
        )
        charactersViewModel.uiState.test {
            Assertions.assertEquals(CharactersState.Loading(false), awaitItem())

            createTestCharactersResponses().map {
                CharacterMapper.transform(it)
            }.let { expectedCharacters ->
                Assertions.assertEquals(
                    CharactersState.Loaded(false, expectedCharacters),
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `When vm obtained RetryButtonClicked event vm call loadCharacters and uiState return loading and loaded state`() = runTest {
        Mockito.`when`(charactersRepository.loadCharacters(anyString())).then {
            throw Exception()
        }.thenReturn(
            createTestCharactersResponses()
        )

        val expectedCharacters =
            createTestCharactersResponses().map {
                CharacterMapper.transform(it)
            }

        charactersViewModel.uiState.test {
            ignoreInitStates(this)

            charactersViewModel.obtainEvent(CharactersEvent.RetryButtonClicked)
            Assertions.assertEquals(CharactersState.Loading(false), awaitItem())

            Assertions.assertEquals(
                CharactersState.Loaded(false, expectedCharacters),
                awaitItem()
            )

            verify(charactersRepository, times(2)).loadCharacters(anyString())
        }
    }

    @Test
    fun `When vm obtained ItemDeleted event uiState should return state without deleted characters`() = runTest {
        Mockito.`when`(charactersRepository.loadCharacters(anyString())).thenReturn(
            createTestCharactersResponses()
        )

        val expectedCharacters =
            createTestCharactersResponses().map {
                CharacterMapper.transform(it)
            }.toMutableList().run {
                removeAt(1)
                toList()
            }

        charactersViewModel.uiState.test {
            ignoreInitStates(this)

            charactersViewModel.obtainEvent(CharactersEvent.ItemDeleted(1))

            Assertions.assertEquals(
                CharactersState.Loaded(false, expectedCharacters),
                awaitItem()
            )
        }
    }

    @Test
    fun `When vm obtained SearchInput event vm should call loadCharacters with query argument and uiState should return state with matching characters`() =
        runTest {
            Mockito.`when`(charactersRepository.loadCharacters(eq(""))).thenReturn(
                createTestCharactersResponses()
            )

            Mockito.`when`(charactersRepository.loadCharacters(eq("Walter"))).thenReturn(
                listOf(
                    CharacterResponse(
                        charId = 0,
                        name = "Walter White",
                        nickname = "Heisenberg"
                    )
                )
            )

            val expectedCharacters = listOf(
                Character(
                    0,
                    "Walter White",
                    nickname = "Heisenberg",
                    ""
                )
            )

            charactersViewModel.uiState.test {
                ignoreInitStates(this)

                charactersViewModel.obtainEvent(CharactersEvent.SearchInput("Walter"))

                Assertions.assertEquals(
                    CharactersState.Loaded(false, expectedCharacters),
                    awaitItem()
                )

                verify(charactersRepository).loadCharacters(eq("Walter"))
            }
        }

    @Test
    fun `When vm obtained SearchButtonClicked and ToolbarBackButtonClicked events uiState should return Loaded state with correct isOpenedSearchBar`() =
        runTest {
            Mockito.`when`(charactersRepository.loadCharacters(anyString())).thenReturn(
                createTestCharactersResponses()
            )

            val expectedCharacters =
                createTestCharactersResponses().map {
                    CharacterMapper.transform(it)
                }

            charactersViewModel.uiState.test {
                ignoreInitStates(this)

                charactersViewModel.obtainEvent(CharactersEvent.SearchButtonClicked)

                Assertions.assertEquals(
                    CharactersState.Loaded(true, expectedCharacters),
                    awaitItem()
                )

                charactersViewModel.obtainEvent(CharactersEvent.ToolbarBackButtonClicked)

                Assertions.assertEquals(
                    CharactersState.Loaded(false, expectedCharacters),
                    awaitItem()
                )
            }
        }

    @Test
    fun `When loadCharacters method throws exception uiState should return Error state`() = runTest {
        Mockito.`when`(charactersRepository.loadCharacters(anyString())).then {
            throw Exception("Ошибка")
        }

        charactersViewModel.uiState.test {
            Assertions.assertEquals(CharactersState.Loading(false), awaitItem())

            Assertions.assertEquals(
                CharactersState.Error(false, UiText.WithString("Ошибка")),
                awaitItem()
            )
        }
    }

     private suspend fun ignoreInitStates(receiveTurbine: ReceiveTurbine<CharactersState>) {
         receiveTurbine.awaitItem()
         receiveTurbine.awaitItem()
     }

    private fun createTestCharactersResponses(): List<CharacterResponse> {
        return listOf(
            CharacterResponse(
                charId = 0,
                name = "Walter White",
                nickname = "Heisenberg"
            ),
            CharacterResponse(
                charId = 1,
                name = "Jesse Pinkman",
                nickname = "Cap n' Cook"
            ),
            CharacterResponse(
                charId = 2,
                name = "Skyler White",
                nickname = "Sky"
            ),
        )
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Dispatchers.setMain(StandardTestDispatcher())
        }

        @AfterAll
        @JvmStatic
        fun after() {
            Dispatchers.resetMain()
        }
    }
}