package ru.alexeypodusov.sample649.base.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel<VS, VE> : ViewModel() {
    protected val _singleActions = MutableSharedFlow<SingleAction>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val singleActions = _singleActions.asSharedFlow()

    abstract val uiState: StateFlow<VS>

    abstract fun obtainEvent(event: VE)
}