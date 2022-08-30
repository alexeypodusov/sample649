package ru.alexeypodusov.sample649.di

import dagger.Component
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersDependencies
import ru.alexeypodusov.sample649.data.di.RepositoriesModule
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepository
import javax.inject.Singleton

@Component(modules = [RepositoriesModule::class])
@Singleton
interface AppComponent: CharactersDependencies {
    override val charactersRepository: CharactersRepository
}