package com.example.pettycash.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> CollectOneTimeEvent(flow: Flow<T>, onEvent: (T) -> Unit) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(flow) {
        flow.collect { currentOnEvent(it) }
    }
}
