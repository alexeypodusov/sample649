package ru.alexeypodusov.sample649.data.storage.network.breakingbad.entities

import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("char_id")
    val charId: Int,
    val name: String = "",
    val birthday: String? = null,
    val occupation: List<String> = listOf(),
    val img: String = "",
    val status: String? = null,
    val appearance: List<Int> = listOf(),
    val nickname: String = "",
    val portrayed: String = "",
    val category: String? = null,
    @SerializedName("better_call_saul_appearance")
    val betterCallSaulAppearance: List<Int>? = null
)