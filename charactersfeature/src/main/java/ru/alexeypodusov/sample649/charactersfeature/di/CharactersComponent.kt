package ru.alexeypodusov.sample649.charactersfeature.di

import dagger.Component
import ru.alexeypodusov.sample649.charactersfeature.presentation.CharactersViewModel
import ru.alexeypodusov.sample649.charactersfeature.presentation.view.CharactersFragment
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepository
import javax.inject.Scope

@Component(dependencies = [CharactersDependencies::class])
@CharactersScope
interface CharactersComponent {
    @CharactersScope
    val viewModelFactory: CharactersViewModel.Factory

    fun inject(fragment: CharactersFragment)

    @Component.Builder
    interface Builder {
        fun characterDependencies(characterDependencies: CharactersDependencies): Builder
        fun build(): CharactersComponent
    }
}

interface CharactersDependencies {
    val charactersRepository: CharactersRepository
}

interface CharactersComponentProvider {
    fun provideCharactersComponent(): CharactersComponent
}

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class CharactersScope