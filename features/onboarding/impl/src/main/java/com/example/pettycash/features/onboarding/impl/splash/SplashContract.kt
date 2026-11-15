@file:Suppress("MatchingDeclarationName")

package com.example.pettycash.features.onboarding.impl.splash

internal sealed interface SplashEvent {
    data object NavigateToIntro : SplashEvent
}
