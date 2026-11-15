package com.example.pettycash.features.company.impl.di

import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.features.company.api.CompanyOnboardingNavKey
import com.example.pettycash.features.company.impl.data.CompanyRepository
import com.example.pettycash.features.company.impl.data.LocalCompanyRepository
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingRoute
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingViewModel
import com.example.pettycash.features.home.api.HomeNavKey
import com.example.pettycash.presentation.LocalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val CompanyImplModule = module {
    single { CompanyLocalDataSource(androidContext()) }
    single<CompanyRepository> { LocalCompanyRepository(get()) }

    viewModelOf(::CompanyOnboardingViewModel)

    navigation<CompanyOnboardingNavKey> {
        val navigator = LocalNavigator.current
        CompanyOnboardingRoute(
            onNavigateHome = { navigator.replaceAll(HomeNavKey) },
        )
    }
}
