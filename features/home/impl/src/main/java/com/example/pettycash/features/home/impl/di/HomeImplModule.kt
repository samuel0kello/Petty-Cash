package com.example.pettycash.features.home.impl.di

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.features.company.api.CompanyOnboardingNavKey
import com.example.pettycash.features.home.api.HomeActivityNavKey
import com.example.pettycash.features.home.api.HomeFloatNavKey
import com.example.pettycash.features.home.api.HomeHistoryNavKey
import com.example.pettycash.features.home.api.HomeMoreNavKey
import com.example.pettycash.features.home.api.HomeNavKey
import com.example.pettycash.features.home.api.HomeNewRequestNavKey
import com.example.pettycash.features.requests.api.ApprovalsNavKey
import com.example.pettycash.features.requests.api.RequestCashNavKey
import com.example.pettycash.features.home.api.HomeProfileNavKey
import com.example.pettycash.features.home.api.HomeReconciliationNavKey
import com.example.pettycash.features.home.api.HomeRecordSpendNavKey
import com.example.pettycash.features.home.api.HomeRequestsNavKey
import com.example.pettycash.features.home.impl.data.HomeRepository
import com.example.pettycash.features.home.impl.data.LocalHomeRepository
import com.example.pettycash.features.home.impl.data.HomeTab
import com.example.pettycash.features.home.impl.ui.HomeEvent
import com.example.pettycash.features.home.impl.ui.HomeRoute
import com.example.pettycash.features.home.impl.ui.HomeViewModel
import com.example.pettycash.features.invite.api.TeamNavKey
import com.example.pettycash.presentation.LocalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val HomeImplModule = module {
    single { HomeLocalDataSource(androidContext()) }
    single<HomeRepository> { LocalHomeRepository(get()) }

    viewModelOf(::HomeViewModel)

    navigation<HomeNavKey> {
        val navigator = LocalNavigator.current
        HomeRoute(
            onHomeEvent = { event ->
                when (event) {
                    is HomeEvent.NavigateToTab -> when (event.tab) {
                        HomeTab.Home -> navigator.goBack()
                        HomeTab.Approvals -> navigator.goTo(ApprovalsNavKey)
                        HomeTab.Float -> navigator.goTo(HomeFloatNavKey)
                        HomeTab.People -> navigator.goTo(TeamNavKey)
                        HomeTab.More -> navigator.goTo(HomeMoreNavKey)
                        HomeTab.Requests -> navigator.goTo(HomeRequestsNavKey)
                        HomeTab.History -> navigator.goTo(HomeHistoryNavKey)
                        HomeTab.Profile -> navigator.goTo(HomeProfileNavKey)
                    }
                    HomeEvent.NavigateToRequestCash -> navigator.goTo(RequestCashNavKey)
                    HomeEvent.NavigateToClaimRefund -> navigator.goTo(HomeNewRequestNavKey)
                    HomeEvent.NavigateToRecordSpend -> navigator.goTo(HomeRecordSpendNavKey)
                    HomeEvent.NavigateToApprovals -> navigator.goTo(ApprovalsNavKey)
                    HomeEvent.NavigateToFloat -> navigator.goTo(HomeFloatNavKey)
                    HomeEvent.NavigateToPeople -> navigator.goTo(TeamNavKey)
                    HomeEvent.NavigateToMore -> navigator.goTo(HomeMoreNavKey)
                    HomeEvent.NavigateToRequests -> navigator.goTo(HomeRequestsNavKey)
                    HomeEvent.NavigateToHistory -> navigator.goTo(HomeHistoryNavKey)
                    HomeEvent.NavigateToProfile -> navigator.goTo(HomeProfileNavKey)
                    HomeEvent.NavigateToActivity -> navigator.goTo(HomeActivityNavKey)
                    HomeEvent.NavigateToReconciliation -> navigator.goTo(HomeReconciliationNavKey)
                    HomeEvent.NavigateToCompanyOnboarding -> navigator.goTo(CompanyOnboardingNavKey)
                }
            },
        )
    }

    navigation<HomeFloatNavKey> { HomeStubScreen("Float") }
    navigation<HomeMoreNavKey> { HomeStubScreen("More") }
    navigation<HomeRequestsNavKey> { HomeStubScreen("Requests") }
    navigation<HomeHistoryNavKey> { HomeStubScreen("History") }
    navigation<HomeProfileNavKey> { HomeStubScreen("Profile") }
    navigation<HomeNewRequestNavKey> { HomeStubScreen("Claim a refund") }
    navigation<HomeRecordSpendNavKey> { HomeStubScreen("Record a spend") }
    navigation<HomeActivityNavKey> { HomeStubScreen("Activity") }
    navigation<HomeReconciliationNavKey> { HomeStubScreen("Reconciliation") }
}

@Composable
private fun HomeStubScreen(title: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(contentAlignment = Alignment.Center) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
