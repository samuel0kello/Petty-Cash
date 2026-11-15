package com.example.pettycash.features.requests.impl.ui.request

import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestContext

enum class RequestStep { Amount, Details, Review, Sent }

data class RequestCashState(
    val step: RequestStep = RequestStep.Amount,
    val amountDigits: String = "",
    val context: RequestContext? = null,
    val purpose: String = "",
    val category: String = "",
    val project: String = "",
    val site: String = "",
    val neededBy: String = "",
    val note: String = "",
    val sentRequest: CashRequest? = null,
    val submitting: Boolean = false,
)

sealed interface RequestCashAction {
    data class AmountKey(val key: String) : RequestCashAction
    data object AmountBackspace : RequestCashAction
    data class PurposeChanged(val value: String) : RequestCashAction
    data class CategoryChanged(val value: String) : RequestCashAction
    data class ProjectChanged(val value: String) : RequestCashAction
    data class SiteChanged(val value: String) : RequestCashAction
    data class NeededByChanged(val value: String) : RequestCashAction
    data class NoteChanged(val value: String) : RequestCashAction
    data object Next : RequestCashAction
    data object Back : RequestCashAction
    data object Send : RequestCashAction
    data object Done : RequestCashAction
}

sealed interface RequestCashEvent {
    data object NavigateHome : RequestCashEvent
    data class OpenRequestDetail(val requestId: String) : RequestCashEvent
}
