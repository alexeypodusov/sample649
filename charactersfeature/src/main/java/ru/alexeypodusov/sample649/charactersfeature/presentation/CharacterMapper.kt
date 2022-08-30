package ru.alexeypodusov.sample649.charactersfeature.presentation

import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse
import ru.alexeypodusov.sample649.base.util.Mapper

internal object CharacterMapper: Mapper<CharacterResponse, Character> {
    override fun transform(data: CharacterResponse): Character {
        return Character(
            data.charId,
            data.name,
            data.nickname,
            data.img
        )
    }

}