package com.example.pettycash.features.home.impl.ui

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.home.impl.data.HomeRepository
import com.example.pettycash.features.home.impl.data.HomeTab
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val homeRepository: HomeRepository,
) : MviViewModel<HomeAction, HomeEvent, HomeState>(HomeState()) {

    override fun onStateSubscription() {
        viewModelScope.launch {
            homeRepository.role.collectLatest { role ->
                val dashboard = homeRepository.dashboard(role)
                updateState { it.copy(dashboard = dashboard, roleSwitcherOpen = false) }
            }
        }
        viewModelScope.launch {
            homeRepository.coachMarkPending.collectLatest { pending ->
                updateState { it.copy(showCoachMark = pending) }
            }
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnGreetingClick -> {
                if ((currentState.dashboard?.switchableRoles?.size ?: 0) > 1) {
                    updateState { it.copy(roleSwitcherOpen = !it.roleSwitcherOpen) }
                }
            }
            HomeAction.OnDismissRoleSwitcher -> updateState { it.copy(roleSwitcherOpen = false) }
            is HomeAction.OnRoleSelect -> selectRole(action.index)
            is HomeAction.OnTabSelect -> sendEvent(HomeEvent.NavigateToTab(action.tab))
            HomeAction.OnFabClick -> updateState { it.copy(newSheetOpen = true) }
            HomeAction.OnNewSheetDismiss -> updateState { it.copy(newSheetOpen = false) }
            HomeAction.OnSheetRequestCash -> {
                updateState { it.copy(newSheetOpen = false) }
                sendEvent(HomeEvent.NavigateToRequestCash)
            }
            HomeAction.OnSheetRecordSpend -> {
                updateState { it.copy(newSheetOpen = false) }
                sendEvent(HomeEvent.NavigateToRecordSpend)
            }
            HomeAction.OnSheetClaimRefund -> {
                updateState { it.copy(newSheetOpen = false) }
                sendEvent(HomeEvent.NavigateToClaimRefund)
            }
            HomeAction.OnBalanceClick, HomeAction.OnTopUpClick -> sendEvent(HomeEvent.NavigateToFloat)
            HomeAction.OnApprovalCardClick -> sendEvent(HomeEvent.NavigateToApprovals)
            HomeAction.OnUnaccountedCardClick -> sendEvent(HomeEvent.NavigateToReconciliation)
            HomeAction.OnReceiptsCardClick -> sendEvent(HomeEvent.NavigateToActivity)
            HomeAction.OnRowClick -> sendEvent(HomeEvent.NavigateToActivity)
            is HomeAction.OnSetupStepClick -> {
                val steps = currentState.dashboard?.setupSteps.orEmpty()
                val pendingIndex = steps.indexOfFirst { !it.done }
                if (action.index == pendingIndex && pendingIndex >= 0) {
                    viewModelScope.launch { homeRepository.dismissFirstVisit() }
                    sendEvent(HomeEvent.NavigateToCompanyOnboarding)
                }
            }
            HomeAction.OnCoachMarkDismiss -> {
                viewModelScope.launch { homeRepository.dismissCoachMark() }
            }
        }
    }

    private fun selectRole(index: Int) {
        val roles = currentState.dashboard?.switchableRoles ?: return
        val role = roles.getOrNull(index) ?: return
        if (role == currentState.dashboard?.role) {
            updateState { it.copy(roleSwitcherOpen = false) }
            return
        }
        viewModelScope.launch {
            homeRepository.selectRole(role)
        }
    }
}
