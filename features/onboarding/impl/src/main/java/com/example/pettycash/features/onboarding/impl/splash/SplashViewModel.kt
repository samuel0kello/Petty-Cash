package com.example.pettycash.features.onboarding.impl.splash

import androidx.lifecycle.viewModelScope
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class SplashViewModel : MviViewModel<Nothing, SplashEvent, Unit>(Unit) {

    override fun onAction(action: Nothing) = Unit

    override fun onStateSubscription() {
        viewModelScope.launch {
            delay(SPLASH_DURATION_MS.milliseconds)
            sendEvent(SplashEvent.NavigateToIntro)
        }
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1500L
    }
}
