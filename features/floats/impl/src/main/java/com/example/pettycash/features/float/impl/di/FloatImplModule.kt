package com.example.pettycash.features.floats.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.floats.api.FloatDetailNavKey
import com.example.pettycash.features.floats.api.FloatsNavKey
import com.example.pettycash.features.floats.api.TopUpNavKey
import com.example.pettycash.features.floats.impl.data.FloatRepository
import com.example.pettycash.features.floats.impl.data.LocalFloatRepository
import com.example.pettycash.features.floats.impl.ui.floats.FloatDetailEvent
import com.example.pettycash.features.floats.impl.ui.floats.FloatDetailRoute
import com.example.pettycash.features.floats.impl.ui.floats.FloatDetailViewModel
import com.example.pettycash.features.floats.impl.ui.floats.FloatsEvent
import com.example.pettycash.features.floats.impl.ui.floats.FloatsRoute
import com.example.pettycash.features.floats.impl.ui.floats.FloatsViewModel
import com.example.pettycash.features.floats.impl.ui.floats.TopUpEvent
import com.example.pettycash.features.floats.impl.ui.floats.TopUpRoute
import com.example.pettycash.features.floats.impl.ui.floats.TopUpViewModel
import com.example.pettycash.features.reconciliation.api.ReconciliationNavKey
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val FloatImplModule = module {
    single { ActivityLocalDataSource(androidContext()) }
    single<FloatRepository> {
        LocalFloatRepository(
            get(),
            get<CompanyLocalDataSource>(),
            get<InviteLocalDataSource>(),
            Json { ignoreUnknownKeys = true },
        )
    }

    viewModelOf(::FloatsViewModel)
    viewModelOf(::FloatDetailViewModel)
    viewModelOf(::TopUpViewModel)

    navigation<FloatsNavKey> {
        val navigator = LocalNavigator.current
        FloatsRoute(
            onFloatsEvent = {
                    when (it) {
                        is FloatsEvent.OpenFloatDetail ->
                            navigator.goTo(FloatDetailNavKey)
                    }
            },
        )
    }

    navigation<FloatDetailNavKey> {
        val navigator = LocalNavigator.current
        FloatDetailRoute(
            onBack = { navigator.goBack() },
            onFloatDetailEvent = {
                when (it) {
                    FloatDetailEvent.TopUp -> navigator.goTo(TopUpNavKey)
                    FloatDetailEvent.StartReconciliation -> navigator.goTo(ReconciliationNavKey)
                }
            },
        )
    }

    navigation<TopUpNavKey> {
        val navigator = LocalNavigator.current
        TopUpRoute(
            onBack = { navigator.goBack() },
            onTopUpEvent = {
                when (it) {
                    is TopUpEvent.Saved -> navigator.goBack()
                    TopUpEvent.Back -> navigator.goBack()
                }
            },
        )
    }
}
