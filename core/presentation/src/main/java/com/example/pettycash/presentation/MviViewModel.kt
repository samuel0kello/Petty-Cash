package com.example.pettycash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<Action, Event, State>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    private val _event = Channel<Event>(Channel.BUFFERED)
    private var didSubscribe = false

    val state: Flow<State> = _state
        .onStart {
            if (!didSubscribe) {
                didSubscribe = true
                onStateSubscription()
            }
        }

    val event: Flow<Event> = _event.receiveAsFlow()

    protected val currentState: State get() = _state.value

    abstract fun onAction(action: Action)

    protected open fun onStateSubscription() = Unit

    protected fun updateState(reducer: (State) -> State) {
        _state.value = reducer(_state.value)
    }

    protected fun sendEvent(event: Event) {
        viewModelScope.launch { _event.send(event) }
    }
}
