package com.example.pettycash.di

import com.example.pettycash.features.auth.impl.di.AuthImplModule
import com.example.pettycash.features.company.impl.di.CompanyImplModule
import com.example.pettycash.features.home.impl.di.HomeImplModule
import com.example.pettycash.features.invite.impl.di.InviteImplModule
import com.example.pettycash.features.onboarding.impl.di.OnboardingImplModule
import com.example.pettycash.navigation.PlaceholderNavKey
import com.example.pettycash.ui.PlaceholderScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val AppModule = module {
    includes(OnboardingImplModule, AuthImplModule, CompanyImplModule, HomeImplModule, InviteImplModule)

    navigation<PlaceholderNavKey> { PlaceholderScreen() }
}
