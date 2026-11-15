package com.example.pettycash.di

import com.example.pettycash.features.auth.impl.di.AuthImplModule
import com.example.pettycash.features.company.impl.di.CompanyImplModule
import com.example.pettycash.features.home.impl.di.HomeImplModule
import com.example.pettycash.features.invite.impl.di.InviteImplModule
import com.example.pettycash.features.onboarding.impl.di.OnboardingImplModule
import com.example.pettycash.features.floats.impl.di.FloatImplModule
import com.example.pettycash.features.reconciliation.impl.di.ReconciliationImplModule
import com.example.pettycash.features.records.impl.di.RecordsImplModule
import com.example.pettycash.features.refunds.impl.di.RefundsImplModule
import com.example.pettycash.features.requests.impl.di.RequestsImplModule
import com.example.pettycash.features.spends.impl.di.SpendsImplModule
import com.example.pettycash.navigation.PlaceholderNavKey
import com.example.pettycash.ui.PlaceholderScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val AppModule = module {
    includes(OnboardingImplModule, AuthImplModule, CompanyImplModule, HomeImplModule, InviteImplModule, RequestsImplModule, SpendsImplModule, RefundsImplModule, FloatImplModule, ReconciliationImplModule, RecordsImplModule)

    navigation<PlaceholderNavKey> { PlaceholderScreen() }
}
