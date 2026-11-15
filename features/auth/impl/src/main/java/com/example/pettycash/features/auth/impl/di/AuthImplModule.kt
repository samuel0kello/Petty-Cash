package com.example.pettycash.features.auth.impl.di

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.features.auth.api.AuthConfirmPinNavKey
import com.example.pettycash.features.auth.api.AuthCreatePinNavKey
import com.example.pettycash.features.auth.api.AuthFingerprintOfferNavKey
import com.example.pettycash.features.auth.api.AuthNewPhoneNavKey
import com.example.pettycash.features.auth.api.AuthPhoneNumberNavKey
import com.example.pettycash.features.auth.api.AuthSmsCodeNavKey
import com.example.pettycash.features.auth.api.AuthUnlockNavKey
import com.example.pettycash.features.auth.impl.confirmpin.ConfirmPinRoute
import com.example.pettycash.features.auth.impl.createpin.CreatePinRoute
import com.example.pettycash.features.auth.impl.createpin.CreatePinViewModel
import com.example.pettycash.features.auth.impl.confirmpin.ConfirmPinViewModel
import com.example.pettycash.features.auth.impl.data.AuthRepository
import com.example.pettycash.features.auth.impl.data.LocalAuthRepository
import com.example.pettycash.features.auth.impl.fingerprint.FingerprintRoute
import com.example.pettycash.features.auth.impl.newphone.NewPhoneRoute
import com.example.pettycash.features.auth.impl.phonenumber.PhoneNumberRoute
import com.example.pettycash.features.auth.impl.phonenumber.PhoneNumberViewModel
import com.example.pettycash.features.auth.impl.smscode.SmsCodeRoute
import com.example.pettycash.features.auth.impl.smscode.SmsCodeViewModel
import com.example.pettycash.features.auth.impl.unlock.UnlockRoute
import com.example.pettycash.features.auth.impl.unlock.UnlockViewModel
import com.example.pettycash.features.company.api.CompanyOnboardingNavKey
import com.example.pettycash.features.home.api.HomeNavKey
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val AuthImplModule = module {
    single { AuthLocalDataSource(androidContext()) }
    single<AuthRepository> { LocalAuthRepository(get()) }

    viewModelOf(::PhoneNumberViewModel)
    viewModelOf(::SmsCodeViewModel)
    viewModelOf(::CreatePinViewModel)
    viewModelOf(::ConfirmPinViewModel)
    viewModelOf(::UnlockViewModel)

    navigation<AuthPhoneNumberNavKey> {
        val navigator = LocalNavigator.current
        PhoneNumberRoute(
            onBack = { navigator.goBack() },
            onNavigateToSmsCode = { navigator.goTo(AuthSmsCodeNavKey) },
        )
    }

    navigation<AuthSmsCodeNavKey> {
        val navigator = LocalNavigator.current
        SmsCodeRoute(
            onBack = { navigator.goBack() },
            onNavigateToCreatePin = { navigator.goTo(AuthCreatePinNavKey) },
            onNavigateToUnlock = { navigator.goTo(AuthUnlockNavKey) },
        )
    }

    navigation<AuthCreatePinNavKey> {
        val navigator = LocalNavigator.current
        CreatePinRoute(
            onBack = { navigator.goBack() },
            onNavigateToConfirmPin = { navigator.goTo(AuthConfirmPinNavKey) },
        )
    }

    navigation<AuthConfirmPinNavKey> {
        val navigator = LocalNavigator.current
        ConfirmPinRoute(
            onBack = { navigator.goBack() },
            onNavigateToFingerprintOffer = { navigator.goTo(AuthFingerprintOfferNavKey) },
        )
    }

    navigation<AuthFingerprintOfferNavKey> {
        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()
        val authRepository = koinInject<AuthRepository>()
        val destination =
            if (authRepository.isNewUser()) CompanyOnboardingNavKey else HomeNavKey
        FingerprintRoute(
            onBack = { navigator.goBack() },
            onTurnOn = {
                scope.launch {
                    authRepository.setFingerprintEnabled(true)
                    navigator.replaceAll(destination)
                }
            },
            onNotNow = { navigator.replaceAll(destination) },
        )
    }

    navigation<AuthUnlockNavKey> {
        val navigator = LocalNavigator.current
        UnlockRoute(
            onBack = { navigator.goBack() },
            onNavigateHome = { navigator.replaceAll(HomeNavKey) },
            onForgotPin = { navigator.goTo(AuthSmsCodeNavKey) },
        )
    }

    navigation<AuthNewPhoneNavKey> {
        val navigator = LocalNavigator.current
        NewPhoneRoute(
            onBack = { navigator.goBack() },
            onContinue = { navigator.replaceAll(HomeNavKey) },
        )
    }
}
