package com.example.pettycash.features.refunds.impl.ui.claim

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.refunds.impl.data.RefundsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ClaimRefundViewModel(
    private val repository: RefundsRepository,
) : MviViewModel<ClaimRefundAction, ClaimRefundEvent, ClaimRefundState>(ClaimRefundState()) {

    init {
        repository.snapshot
            .onEach { (_, context) ->
                updateState { it.copy(context = context, refundTo = it.refundTo.ifBlank { context.memberPhone }) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ClaimRefundAction) {
        when (action) {
            is ClaimRefundAction.AmountKey -> updateState {
                it.copy(amountDigits = (it.amountDigits + action.key).take(9))
            }
            ClaimRefundAction.AmountBackspace -> updateState {
                it.copy(amountDigits = it.amountDigits.dropLast(1))
            }
            is ClaimRefundAction.PurposeChanged -> updateState { it.copy(purpose = action.value) }
            is ClaimRefundAction.CategoryChanged -> updateState {
                it.copy(category = action.value, from = "", to = "")
            }
            is ClaimRefundAction.DateChanged -> updateState { it.copy(date = action.value) }
            is ClaimRefundAction.FromChanged -> updateState { it.copy(from = action.value) }
            is ClaimRefundAction.ToChanged -> updateState { it.copy(to = action.value) }
            is ClaimRefundAction.NoteChanged -> updateState { it.copy(note = action.value) }
            ClaimRefundAction.TakePhoto -> updateState { it.copy(step = RefundStep.Capture) }
            ClaimRefundAction.NoReceiptProof -> updateState { it.copy(proof = ProofChoice.NoReceipt) }
            ClaimRefundAction.Shutter -> updateState { it.copy(step = RefundStep.Preview) }
            ClaimRefundAction.UsePhoto -> updateState {
                it.copy(proof = ProofChoice.Photo, step = RefundStep.Proof)
            }
            ClaimRefundAction.Retake -> updateState { it.copy(step = RefundStep.Capture) }
            ClaimRefundAction.ChangeRefundTo -> updateState { it.copy(refundToEditing = true) }
            is ClaimRefundAction.RefundToChanged -> updateState { it.copy(refundTo = action.value) }
            ClaimRefundAction.RefundToDone -> updateState { it.copy(refundToEditing = false) }
            ClaimRefundAction.Next -> onNext()
            ClaimRefundAction.Back -> onBack()
            ClaimRefundAction.Submit -> onSubmit()
        }
    }

    private fun onNext() {
        val state = currentState
        when (state.step) {
            RefundStep.Amount -> if (amount(state) > 0) updateState { it.copy(step = RefundStep.Details) }
            RefundStep.Details -> if (state.purpose.isNotBlank()) updateState { it.copy(step = RefundStep.Proof) }
            RefundStep.Proof -> if (state.proof != ProofChoice.None) updateState { it.copy(step = RefundStep.RefundTo) }
            RefundStep.RefundTo -> if (state.refundTo.isNotBlank()) updateState { it.copy(step = RefundStep.Review) }
            else -> Unit
        }
    }

    private fun onBack() {
        val state = currentState
        when (state.step) {
            RefundStep.Details -> updateState { it.copy(step = RefundStep.Amount) }
            RefundStep.Proof -> updateState { it.copy(step = RefundStep.Details) }
            RefundStep.RefundTo -> updateState { it.copy(step = RefundStep.Proof) }
            RefundStep.Review -> updateState { it.copy(step = RefundStep.RefundTo) }
            RefundStep.Capture, RefundStep.Preview -> updateState { it.copy(step = RefundStep.Proof) }
            RefundStep.Amount -> sendEvent(ClaimRefundEvent.NavigateHome)
        }
    }

    private fun onSubmit() {
        val state = currentState
        if (state.saving) return
        updateState { it.copy(saving = true) }
        viewModelScope.launch {
            repository.saveClaim(
                amountKes = amount(state),
                purpose = state.purpose,
                category = state.category,
                date = state.date,
                from = state.from,
                to = state.to,
                note = state.note,
                proof = when (state.proof) {
                    ProofChoice.Photo -> "Photo"
                    ProofChoice.NoReceipt -> "No receipt"
                    ProofChoice.None -> ""
                },
                refundTo = state.refundTo,
            )
            sendEvent(ClaimRefundEvent.NavigateHome)
        }
    }
}

fun amount(state: ClaimRefundState): Long = state.amountDigits.toLongOrNull() ?: 0L
