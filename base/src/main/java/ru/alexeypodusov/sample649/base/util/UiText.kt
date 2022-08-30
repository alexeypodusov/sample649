package ru.alexeypodusov.sample649.base.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class WithResource(@StringRes val resId: Int): UiText()
    data class WithString(val string: String): UiText()

    fun getString(context: Context): String {
        return when (this) {
            is WithResource -> context.getString(resId)
            is WithString -> string
        }
    }
}