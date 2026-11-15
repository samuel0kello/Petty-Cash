package com.example.pettycash.features.onboarding.impl.di

import com.example.pettycash.features.auth.api.AuthPhoneNumberNavKey
import com.example.pettycash.features.onboarding.api.OnboardingIntroNavKey
import com.example.pettycash.features.onboarding.api.OnboardingSplashNavKey
import com.example.pettycash.features.onboarding.api.OnboardingWelcomeNavKey
import com.example.pettycash.features.onboarding.impl.intro.IntroScreen
import com.example.pettycash.features.onboarding.impl.splash.SplashRoute
import com.example.pettycash.features.onboarding.impl.splash.SplashViewModel
import com.example.pettycash.features.onboarding.impl.welcome.WelcomeScreen
import com.example.pettycash.presentation.LocalNavigator
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val OnboardingImplModule = module {
    viewModelOf(::SplashViewModel)

    navigation<OnboardingSplashNavKey> {
        val navigator = LocalNavigator.current
        SplashRoute(onNavigateToIntro = { navigator.goTo(OnboardingIntroNavKey) })
    }

    navigation<OnboardingIntroNavKey> {
        val navigator = LocalNavigator.current
        IntroScreen(
            onFinish = { navigator.goTo(OnboardingWelcomeNavKey) },
            onSkip = { navigator.goTo(OnboardingWelcomeNavKey) },
        )
    }

    navigation<OnboardingWelcomeNavKey> {
        val navigator = LocalNavigator.current
        WelcomeScreen(
            onSetUpCompany = { navigator.goTo(AuthPhoneNumberNavKey) },
            onInvited = { navigator.goTo(AuthPhoneNumberNavKey) },
            onExistingAccount = { navigator.goTo(AuthPhoneNumberNavKey) },
        )
    }
}
