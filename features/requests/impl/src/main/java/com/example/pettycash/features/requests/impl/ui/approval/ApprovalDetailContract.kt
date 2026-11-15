package com.example.pettycash.features.requests.impl.ui.approval

import com.example.pettycash.features.requests.impl.data.CashRequest

enum class ApprovalSheet { None, AskInfo, Reject, PayoutReview, PinEntry, Waiting, Paid, Failed }

data class ApprovalDetailState(
    val request: CashRequest? = null,
    val loaded: Boolean = false,
    val sheet: ApprovalSheet = ApprovalSheet.None,
    val askText: String = "",
    val rejectReason: String = "",
    val pinEntered: Int = 0,
    val pinError: Boolean = false,
    val duplicateVisible: Boolean = false,
    val rejectedSnackbar: Boolean = false,
    val payoutReference: String = "",
    val payoutFeeKes: Long = 22,
)

sealed interface ApprovalDetailAction {
    data object Back : ApprovalDetailAction
    data object Done : ApprovalDetailAction
    data object Approve : ApprovalDetailAction
    data object AskInfo : ApprovalDetailAction
    data class AskTextChanged(val value: String) : ApprovalDetailAction
    data object AskInfoSend : ApprovalDetailAction
    data object Reject : ApprovalDetailAction
    data class RejectReasonChanged(val value: String) : ApprovalDetailAction
    data object RejectSend : ApprovalDetailAction
    data object RejectDismiss : ApprovalDetailAction
    data object RejectUndo : ApprovalDetailAction
    data object SnackbarDismiss : ApprovalDetailAction
    data object PayoutConfirm : ApprovalDetailAction
    data class PinDigit(val digit: Int) : ApprovalDetailAction
    data object PinDelete : ApprovalDetailAction
    data object PayoutCancel : ApprovalDetailAction
    data object DuplicateCancel : ApprovalDetailAction
    data object DuplicateConfirm : ApprovalDetailAction
}

sealed interface ApprovalDetailEvent {
    data object NavigateBack : ApprovalDetailEvent
    data object NavigateHome : ApprovalDetailEvent
}
