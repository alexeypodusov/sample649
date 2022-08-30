package ru.alexeypodusov.sample649.base.util

import ru.alexeypodusov.sample649.base.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

fun Exception.uiText(): UiText {
    return when(this) {
        is SocketTimeoutException -> {
            UiText.WithResource(R.string.request_network_timeout)
        }
        is ConnectException -> {
            UiText.WithResource(R.string.request_failure_network_exception)
        }
        is UnknownHostException -> {
            UiText.WithResource(R.string.request_failure_no_network_exception)
        }
        is SSLPeerUnverifiedException -> {
            UiText.WithResource(R.string.request_connection_close_by_peer)
        }
        is SSLHandshakeException -> {
            UiText.WithResource(R.string.request_connection_reset_by_peer)
        }
        else -> {
            message?.takeIf { it.isNotEmpty() }
                ?.let { UiText.WithString(it) }
                ?: UiText.WithResource(R.string.unknown_error)
        }
    }
}