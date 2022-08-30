package ru.alexeypodusov.sample649.data.repositories.characters

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.BreakingBadNetworkStorage
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

class CharactersRepositoryImpl(
    private val breakingBadNetworkStorage: BreakingBadNetworkStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharactersRepository {

    override suspend fun loadCharacters(query: String): List<CharacterResponse> = withContext(ioDispatcher) {
        breakingBadNetworkStorage.characters(query)
    }
}
