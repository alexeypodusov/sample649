package ru.alexeypodusov.sample649.data.storage.network.breakingbad

import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

interface BreakingBadNetworkStorage {
    suspend fun characters(name: String): List<CharacterResponse>
}
