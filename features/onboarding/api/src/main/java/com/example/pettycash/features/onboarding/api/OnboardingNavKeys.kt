package com.example.pettycash.features.onboarding.api

import androidx.navigation3.runtime.NavKey

/** App start: brand splash, navigates onward after ~1.5s. */
data object OnboardingSplashNavKey : NavKey

/** Three-page intro pager (F01.2–F01.4). */
data object OnboardingIntroNavKey : NavKey

/** "How will you use PettyCash?" chooser (F01.5). */
data object OnboardingWelcomeNavKey : NavKey
