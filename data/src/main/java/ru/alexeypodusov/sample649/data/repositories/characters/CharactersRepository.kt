package ru.alexeypodusov.sample649.data.repositories.characters

import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

interface CharactersRepository {
    suspend fun loadCharacters(query: String): List<CharacterResponse>
}