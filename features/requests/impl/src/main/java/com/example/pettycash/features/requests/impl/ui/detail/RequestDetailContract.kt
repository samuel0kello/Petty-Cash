package com.example.pettycash.features.requests.impl.ui.detail

import com.example.pettycash.features.requests.impl.data.CashRequest

data class RequestDetailState(
    val request: CashRequest? = null,
    val loaded: Boolean = false,
)

sealed interface RequestDetailAction {
    data object Back : RequestDetailAction
    data object AddReceipt : RequestDetailAction
    data object Withdraw : RequestDetailAction
    data object Done : RequestDetailAction
}

sealed interface RequestDetailEvent {
    data object NavigateBack : RequestDetailEvent
    data object NavigateHome : RequestDetailEvent
}
