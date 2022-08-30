package ru.alexeypodusov.sample649.data.storage.network.breakingbad.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities.CharacterResponse

interface BreakingBadApi {
    @GET("characters")
    suspend fun characters(@Query("name") name: String): List<CharacterResponse>
}