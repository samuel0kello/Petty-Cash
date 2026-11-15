package com.example.pettycash.features.requests.impl.di

import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.datasource.local.RequestLocalDataSource
import com.example.pettycash.features.requests.api.RequestCashNavKey
import com.example.pettycash.features.requests.api.RequestDetailNavKey
import com.example.pettycash.features.requests.impl.data.LocalRequestsRepository
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailEvent
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailRoute
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailViewModel
import com.example.pettycash.features.requests.impl.ui.request.RequestCashEvent
import com.example.pettycash.features.requests.impl.ui.request.RequestCashRoute
import com.example.pettycash.features.requests.impl.ui.request.RequestCashViewModel
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val RequestsImplModule = module {
    single { RequestLocalDataSource(androidContext()) }
    single<RequestsRepository> {
        LocalRequestsRepository(get(), get<CompanyLocalDataSource>(), get<InviteLocalDataSource>(), Json { ignoreUnknownKeys = true })
    }

    viewModelOf(::RequestCashViewModel)
    viewModel { params -> RequestDetailViewModel(get(), params.get()) }

    navigation<RequestCashNavKey> {
        val navigator = LocalNavigator.current
        RequestCashRoute(
            onRequestCashEvent = { event ->
                when (event) {
                    RequestCashEvent.NavigateHome -> navigator.goBack()
                    is RequestCashEvent.OpenRequestDetail ->
                        navigator.goTo(RequestDetailNavKey(event.requestId))
                }
            },
        )
    }

    navigation<RequestDetailNavKey> { key ->
        val navigator = LocalNavigator.current
        RequestDetailRoute(
            requestId = key.requestId,
            onRequestDetailEvent = { event ->
                when (event) {
                    RequestDetailEvent.NavigateBack,
                    RequestDetailEvent.NavigateHome,
                    -> navigator.goBack()
                }
            },
        )
    }

}
