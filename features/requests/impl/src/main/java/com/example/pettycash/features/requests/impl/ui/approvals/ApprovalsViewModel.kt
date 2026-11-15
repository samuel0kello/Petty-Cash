package com.example.pettycash.features.requests.impl.ui.approvals

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ApprovalsViewModel(
    private val repository: RequestsRepository,
) : MviViewModel<ApprovalsAction, ApprovalsEvent, ApprovalsState>(ApprovalsState()) {

    init {
        repository.snapshot
            .onEach { (requests, _) ->
                updateState { it.copy(requests = requests, loaded = true) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ApprovalsAction) {
        when (action) {
            is ApprovalsAction.OnTabSelect -> updateState { it.copy(tab = action.tab) }
            is ApprovalsAction.OnRequestClick -> sendEvent(ApprovalsEvent.NavigateToDetail(action.requestId))
            is ApprovalsAction.OnNavSelect -> when (action.index) {
                0 -> sendEvent(ApprovalsEvent.NavigateHome)
                2 -> sendEvent(ApprovalsEvent.NavigateToFloat)
                3 -> sendEvent(ApprovalsEvent.NavigateToPeople)
                4 -> sendEvent(ApprovalsEvent.NavigateToMore)
            }
        }
    }
}

fun requestsForTab(state: ApprovalsState, tab: ApprovalTab): List<CashRequest> =
    state.requests.filter { request ->
        when (tab) {
            ApprovalTab.Waiting ->
                request.status == RequestStatus.PENDING || request.status == RequestStatus.INFO_REQUESTED
            ApprovalTab.Approved ->
                request.status == RequestStatus.APPROVED || request.status == RequestStatus.PAID
            ApprovalTab.Rejected -> request.status == RequestStatus.REJECTED
        }
    }

fun waitingTotalKes(state: ApprovalsState): Long =
    requestsForTab(state, ApprovalTab.Waiting).sumOf { it.amountKes }
