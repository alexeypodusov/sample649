package ru.alexeypodusov.sample649.data.di

import dagger.Module
import dagger.Provides
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepository
import ru.alexeypodusov.sample649.data.repositories.characters.CharactersRepositoryImpl
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.BreakingBadNetworkStorage

@Module(includes = [DataStorageModule::class])
class RepositoriesModule {
    @Provides
    fun provideCharactersRepository(breakingBadNetworkStorage: BreakingBadNetworkStorage): CharactersRepository =
        CharactersRepositoryImpl(
            breakingBadNetworkStorage
        )
}