package com.example.pettycash.features.requests.impl.ui.approvals

import com.example.pettycash.features.requests.impl.data.CashRequest

enum class ApprovalTab { Waiting, Approved, Rejected }

data class ApprovalsState(
    val tab: ApprovalTab = ApprovalTab.Waiting,
    val requests: List<CashRequest> = emptyList(),
    val loaded: Boolean = false,
)

sealed interface ApprovalsAction {
    data class OnTabSelect(val tab: ApprovalTab) : ApprovalsAction
    data class OnRequestClick(val requestId: String) : ApprovalsAction
    data class OnNavSelect(val index: Int) : ApprovalsAction
}

sealed interface ApprovalsEvent {
    data class NavigateToDetail(val requestId: String) : ApprovalsEvent
    data object NavigateHome : ApprovalsEvent
    data object NavigateToFloat : ApprovalsEvent
    data object NavigateToPeople : ApprovalsEvent
    data object NavigateToMore : ApprovalsEvent
}
