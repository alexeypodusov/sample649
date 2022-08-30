package ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit

import retrofit2.HttpException
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.BreakingBadNetworkStorage
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.LimitApiException
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

class BreakingBadRetrofitNetworkStorage(
    private val breakingBadApi: BreakingBadApi
) : BreakingBadNetworkStorage {
    override suspend fun characters(name: String): List<CharacterResponse> = wrapHttpException {
        breakingBadApi.characters(name)
    }

    private suspend fun <T> wrapHttpException(block: suspend () -> T): T {
        try {
            return block()
        } catch (e: HttpException) {
            if (e.code() == 429) {
                throw LimitApiException()
            }
            throw e
        }
    }
}