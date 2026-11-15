package com.example.pettycash.features.refunds.impl.ui.claim

import com.example.pettycash.features.refunds.impl.data.RefundContext

enum class RefundStep { Amount, Details, Proof, RefundTo, Review, Capture, Preview }
enum class ProofChoice { None, Photo, NoReceipt }

data class ClaimRefundState(
    val step: RefundStep = RefundStep.Amount,
    val amountDigits: String = "",
    val context: RefundContext? = null,
    val purpose: String = "",
    val category: String = "",
    val date: String = "",
    val from: String = "",
    val to: String = "",
    val note: String = "",
    val proof: ProofChoice = ProofChoice.None,
    val refundTo: String = "",
    val refundToEditing: Boolean = false,
    val saving: Boolean = false,
)

sealed interface ClaimRefundAction {
    data class AmountKey(val key: String) : ClaimRefundAction
    data object AmountBackspace : ClaimRefundAction
    data class PurposeChanged(val value: String) : ClaimRefundAction
    data class CategoryChanged(val value: String) : ClaimRefundAction
    data class DateChanged(val value: String) : ClaimRefundAction
    data class FromChanged(val value: String) : ClaimRefundAction
    data class ToChanged(val value: String) : ClaimRefundAction
    data class NoteChanged(val value: String) : ClaimRefundAction
    data object TakePhoto : ClaimRefundAction
    data object NoReceiptProof : ClaimRefundAction
    data object Shutter : ClaimRefundAction
    data object UsePhoto : ClaimRefundAction
    data object Retake : ClaimRefundAction
    data object ChangeRefundTo : ClaimRefundAction
    data class RefundToChanged(val value: String) : ClaimRefundAction
    data object RefundToDone : ClaimRefundAction
    data object Next : ClaimRefundAction
    data object Back : ClaimRefundAction
    data object Submit : ClaimRefundAction
}

sealed interface ClaimRefundEvent {
    data object NavigateHome : ClaimRefundEvent
}
