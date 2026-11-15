package com.example.pettycash.features.requests.impl.ui.approval

import androidx.lifecycle.viewModelScope
import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ApprovalDetailViewModel(
    private val repository: RequestsRepository,
    private val authLocal: AuthLocalDataSource,
    private val requestId: String,
) : MviViewModel<ApprovalDetailAction, ApprovalDetailEvent, ApprovalDetailState>(ApprovalDetailState()) {

    private var pinValue = ""
    private var previousStatus = RequestStatus.PENDING

    init {
        repository.snapshot
            .onEach { (requests, _) ->
                val request = requests.firstOrNull { it.id == requestId }
                updateState { it.copy(request = request, loaded = true) }
            }
            .launchIn(viewModelScope)
        viewModelScope.launch { pinValue = authLocal.pinValue() ?: "" }
    }

    override fun onAction(action: ApprovalDetailAction) {
        when (action) {
            ApprovalDetailAction.Back -> sendEvent(ApprovalDetailEvent.NavigateBack)
            ApprovalDetailAction.Done -> sendEvent(ApprovalDetailEvent.NavigateHome)
            ApprovalDetailAction.Approve -> onApprove()
            ApprovalDetailAction.AskInfo -> updateState { it.copy(sheet = ApprovalSheet.AskInfo) }
            is ApprovalDetailAction.AskTextChanged -> updateState { it.copy(askText = action.value) }
            ApprovalDetailAction.AskInfoSend -> onAskInfoSend()
            ApprovalDetailAction.Reject -> {
                previousStatus = currentState.request?.status ?: RequestStatus.PENDING
                updateState { it.copy(sheet = ApprovalSheet.Reject) }
            }
            is ApprovalDetailAction.RejectReasonChanged -> updateState { it.copy(rejectReason = action.value) }
            ApprovalDetailAction.RejectSend -> onRejectSend()
            ApprovalDetailAction.RejectDismiss -> updateState { it.copy(sheet = ApprovalSheet.None, rejectReason = "") }
            ApprovalDetailAction.RejectUndo -> onUndoReject()
            ApprovalDetailAction.SnackbarDismiss -> updateState { it.copy(rejectedSnackbar = false) }
            ApprovalDetailAction.PayoutConfirm -> onPayoutConfirm()
            is ApprovalDetailAction.PinDigit -> onPinDigit(action.digit)
            ApprovalDetailAction.PinDelete -> updateState {
                it.copy(pinEntered = (it.pinEntered - 1).coerceAtLeast(0), pinError = false)
            }
            ApprovalDetailAction.PayoutCancel -> {
                pinEntry = ""
                updateState { it.copy(sheet = ApprovalSheet.None, pinEntered = 0, pinError = false) }
            }
            ApprovalDetailAction.DuplicateCancel -> updateState { it.copy(duplicateVisible = false) }
            ApprovalDetailAction.DuplicateConfirm -> {
                pinEntry = ""
                updateState { it.copy(duplicateVisible = false, sheet = ApprovalSheet.PinEntry, pinEntered = 0, pinError = false) }
            }
        }
    }

    private fun onApprove() {
        val request = currentState.request ?: return
        viewModelScope.launch {
            repository.setStatus(request.id, RequestStatus.APPROVED)
            updateState { it.copy(sheet = ApprovalSheet.PayoutReview) }
        }
    }

    private fun onAskInfoSend() {
        val state = currentState
        if (state.askText.isBlank()) return
        val request = state.request ?: return
        viewModelScope.launch {
            repository.setStatus(request.id, RequestStatus.INFO_REQUESTED, reviewNote = state.askText)
            updateState { it.copy(sheet = ApprovalSheet.None, askText = "") }
        }
    }

    private fun onRejectSend() {
        val state = currentState
        if (state.rejectReason.isBlank()) return
        val request = state.request ?: return
        viewModelScope.launch {
            repository.setStatus(request.id, RequestStatus.REJECTED, reviewNote = state.rejectReason)
            updateState { it.copy(sheet = ApprovalSheet.None, rejectReason = "", rejectedSnackbar = true) }
        }
    }

    private fun onUndoReject() {
        val request = currentState.request ?: return
        viewModelScope.launch {
            repository.setStatus(request.id, previousStatus)
            updateState { it.copy(rejectedSnackbar = false) }
        }
    }

    private fun onPayoutConfirm() {
        val request = currentState.request ?: return
        viewModelScope.launch {
            val (requests, _) = repository.snapshot.first()
            val duplicate = requests.any {
                it.id != request.id &&
                    it.amountKes == request.amountKes &&
                    it.purpose == request.purpose &&
                    it.status == RequestStatus.PAID
            }
            if (duplicate) {
                updateState { it.copy(duplicateVisible = true) }
            } else {
                pinEntry = ""
                updateState { it.copy(sheet = ApprovalSheet.PinEntry, pinEntered = 0, pinError = false) }
            }
        }
    }

    private fun onPinDigit(digit: Int) {
        val state = currentState
        if (state.pinEntered >= 4) return
        val entered = state.pinEntered + 1
        pinEntry += digit
        if (entered == 4) {
            viewModelScope.launch {
                if (pinEntry == pinValue) {
                    pinEntry = ""
                    updateState { it.copy(pinEntered = 0, pinError = false, sheet = ApprovalSheet.Waiting) }
                    delay(2000)
                    completePayout()
                } else {
                    pinEntry = ""
                    updateState { it.copy(pinEntered = 0, pinError = true) }
                }
            }
        } else {
            updateState { it.copy(pinEntered = entered, pinError = false) }
        }
    }

    private var pinEntry = ""

    private suspend fun completePayout() {
        val request = currentState.request ?: return
        val reference = "TJK7${(1000..9999).random()}Q"
        repository.setStatus(request.id, RequestStatus.PAID, paidReference = reference)
        updateState { it.copy(sheet = ApprovalSheet.Paid, payoutReference = reference) }
    }
}
