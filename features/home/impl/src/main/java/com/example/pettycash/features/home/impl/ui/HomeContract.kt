package com.example.pettycash.features.home.impl.ui

import com.example.pettycash.features.home.impl.data.HomeDashboard
import com.example.pettycash.features.home.impl.data.HomeTab

data class HomeState(
    val dashboard: HomeDashboard? = null,
    val roleSwitcherOpen: Boolean = false,
    val showCoachMark: Boolean = false,
    val newSheetOpen: Boolean = false,
)

sealed interface HomeAction {
    data object OnGreetingClick : HomeAction
    data object OnDismissRoleSwitcher : HomeAction
    data class OnRoleSelect(val index: Int) : HomeAction
    data class OnTabSelect(val tab: HomeTab) : HomeAction
    data object OnFabClick : HomeAction
    data object OnNewSheetDismiss : HomeAction
    data object OnSheetRequestCash : HomeAction
    data object OnSheetRecordSpend : HomeAction
    data object OnSheetClaimRefund : HomeAction
    data object OnBalanceClick : HomeAction
    data object OnTopUpClick : HomeAction
    data object OnApprovalCardClick : HomeAction
    data object OnUnaccountedCardClick : HomeAction
    data object OnReceiptsCardClick : HomeAction
    data object OnRowClick : HomeAction
    data class OnSetupStepClick(val index: Int) : HomeAction
    data object OnCoachMarkDismiss : HomeAction
}

sealed interface HomeEvent {
    data class NavigateToTab(val tab: HomeTab) : HomeEvent
    data object NavigateToRequestCash : HomeEvent
    data object NavigateToRecordSpend : HomeEvent
    data object NavigateToClaimRefund : HomeEvent
    data object NavigateToApprovals : HomeEvent
    data object NavigateToFloat : HomeEvent
    data object NavigateToPeople : HomeEvent
    data object NavigateToMore : HomeEvent
    data object NavigateToRequests : HomeEvent
    data object NavigateToHistory : HomeEvent
    data object NavigateToProfile : HomeEvent
    data object NavigateToActivity : HomeEvent
    data object NavigateToReconciliation : HomeEvent
    data object NavigateToCompanyOnboarding : HomeEvent
}
