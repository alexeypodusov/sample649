package ru.alexeypodusov.sample649.data.di

import dagger.Module
import dagger.Provides
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.BreakingBadNetworkStorage
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit.BreakingBadApi
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit.BreakingBadApiFactory
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit.BreakingBadRetrofitNetworkStorage
import javax.inject.Singleton

@Module
class DataStorageModule {
    @Provides
    @Singleton
    fun provideBreakingBadApi(): BreakingBadApi = BreakingBadApiFactory.create()

    @Provides
    @Singleton
    fun provideBreakingBadNetworkStorage(breakingBadApi: BreakingBadApi): BreakingBadNetworkStorage =
        BreakingBadRetrofitNetworkStorage(
            breakingBadApi
        )
}