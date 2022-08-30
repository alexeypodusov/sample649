package ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.mock
import retrofit2.HttpException
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.BreakingBadNetworkStorage
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.LimitApiException
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

@OptIn(ExperimentalCoroutinesApi::class)
internal class BreakingBadRetrofitNetworkStorageTest {
    private val api = mock<BreakingBadApi>()

    private lateinit var storage: BreakingBadNetworkStorage

    @BeforeEach
    fun setup() {
        storage =
            BreakingBadRetrofitNetworkStorage(
                api
            )
    }

    @Test
    fun `Method characters() should return same list as retrofit api`() = runTest {
        val expectedResponse = listOf(
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
        Mockito.`when`(api.characters(anyString())).thenReturn(
            listOf(
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
        )

        Assertions.assertIterableEquals(expectedResponse, storage.characters(""))
    }

    @Test
    fun `When retrofit api throws exception method should throws same exception`() = runTest {
        Mockito.`when`(api.characters(anyString())).then {
            throw Exception("Ошибка")
        }

        runCatching { storage.characters("") }
            .onFailure {
                Assertions.assertInstanceOf(Exception::class.java, it)
                Assertions.assertEquals("Ошибка", it.message)
            }.run {
                Assertions.assertEquals(true, this.isFailure)
            }
    }

    @Test
    fun `When retrofit api throws HttpException with code 429 method should throws LimitApiException`() = runTest {
        Mockito.`when`(api.characters(anyString())).then {
            throw mock<HttpException>().also {
                Mockito.`when`(it.code()).thenReturn(429)
            }
        }

        runCatching { storage.characters("") }
            .onFailure {
                Assertions.assertInstanceOf(LimitApiException::class.java, it)
            }.run {
                Assertions.assertEquals(true, this.isFailure)
            }
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