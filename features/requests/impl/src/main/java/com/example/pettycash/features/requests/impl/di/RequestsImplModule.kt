package com.example.pettycash.features.requests.impl.di

import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.datasource.local.RequestLocalDataSource
import com.example.pettycash.features.requests.api.ApprovalDetailNavKey
import com.example.pettycash.features.requests.api.ApprovalsNavKey
import com.example.pettycash.features.requests.api.RequestCashNavKey
import com.example.pettycash.features.requests.api.RequestDetailNavKey
import com.example.pettycash.features.requests.impl.data.LocalRequestsRepository
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailEvent
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailRoute
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailEvent
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailRoute
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailViewModel
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsEvent
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsRoute
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsViewModel
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailViewModel
import com.example.pettycash.features.requests.impl.ui.request.RequestCashEvent
import com.example.pettycash.features.requests.impl.ui.request.RequestCashRoute
import com.example.pettycash.features.requests.impl.ui.request.RequestCashViewModel
import com.example.pettycash.features.floats.api.FloatsNavKey
import com.example.pettycash.features.home.api.HomeMoreNavKey
import com.example.pettycash.features.home.api.HomeNavKey
import com.example.pettycash.features.invite.api.TeamNavKey
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
    viewModelOf(::ApprovalsViewModel)
    viewModel { params -> ApprovalDetailViewModel(get(), get<AuthLocalDataSource>(), params.get()) }

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

    navigation<ApprovalsNavKey> {
        val navigator = LocalNavigator.current
        ApprovalsRoute(
            onApprovalsEvent = { event ->
                when (event) {
                    is ApprovalsEvent.NavigateToDetail ->
                        navigator.goTo(ApprovalDetailNavKey(event.requestId))
                    ApprovalsEvent.NavigateHome -> navigator.goBack()
                    ApprovalsEvent.NavigateToFloat -> navigator.goTo(FloatsNavKey)
                    ApprovalsEvent.NavigateToPeople -> navigator.goTo(TeamNavKey)
                    ApprovalsEvent.NavigateToMore -> navigator.goTo(HomeMoreNavKey)
                }
            },
        )
    }

    navigation<ApprovalDetailNavKey> { key ->
        val navigator = LocalNavigator.current
        ApprovalDetailRoute(
            requestId = key.requestId,
            onApprovalDetailEvent = { event ->
                when (event) {
                    ApprovalDetailEvent.NavigateBack,
                    ApprovalDetailEvent.NavigateHome,
                    -> navigator.goBack()
                }
            },
        )
    }

}
