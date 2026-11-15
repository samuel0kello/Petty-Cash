package com.example.pettycash.features.reconciliation.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.reconciliation.api.ReconciliationNavKey
import com.example.pettycash.features.reconciliation.impl.data.LocalReconciliationRepository
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationRepository
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationRoute
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationViewModel
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val ReconciliationImplModule = module {
    single { ActivityLocalDataSource(androidContext()) }
    single { HomeLocalDataSource(androidContext()) }
    single<ReconciliationRepository> {
        LocalReconciliationRepository(
            get(),
            get<CompanyLocalDataSource>(),
            get<InviteLocalDataSource>(),
            get<HomeLocalDataSource>(),
            Json { ignoreUnknownKeys = true },
        )
    }

    viewModelOf(::ReconciliationViewModel)

    navigation<ReconciliationNavKey> {
        val navigator = LocalNavigator.current
        ReconciliationRoute(
            onBack = { navigator.goBack() },
            onDone = { navigator.goBack() },
        )
    }
}
