package com.example.pettycash.features.requests.impl.ui.request

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RequestCashViewModel(
    private val repository: RequestsRepository,
) : MviViewModel<RequestCashAction, RequestCashEvent, RequestCashState>(RequestCashState()) {

    init {
        repository.snapshot
            .onEach { (requests, context) ->
                updateState { state ->
                    val fresh = state.sentRequest?.let { sent -> requests.firstOrNull { it.id == sent.id } }
                    state.copy(context = context, sentRequest = fresh ?: state.sentRequest)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: RequestCashAction) {
        when (action) {
            is RequestCashAction.AmountKey -> onAmountKey(action.key)
            RequestCashAction.AmountBackspace -> onBackspace()
            is RequestCashAction.PurposeChanged -> updateState { it.copy(purpose = action.value) }
            is RequestCashAction.CategoryChanged -> updateState { it.copy(category = action.value) }
            is RequestCashAction.ProjectChanged -> updateState { it.copy(project = action.value) }
            is RequestCashAction.SiteChanged -> updateState { it.copy(site = action.value) }
            is RequestCashAction.NeededByChanged -> updateState { it.copy(neededBy = action.value) }
            is RequestCashAction.NoteChanged -> updateState { it.copy(note = action.value) }
            RequestCashAction.Next -> onNext()
            RequestCashAction.Back -> onBack()
            RequestCashAction.Send -> onSend()
            RequestCashAction.Done -> sendEvent(RequestCashEvent.NavigateHome)
        }
    }

    private fun onAmountKey(key: String) {
        updateState { state ->
            val digits = state.amountDigits
            if (digits.length >= MAX_DIGITS) return@updateState state
            state.copy(amountDigits = digits + key)
        }
    }

    private fun onBackspace() {
        updateState { it.copy(amountDigits = it.amountDigits.dropLast(1)) }
    }

    private fun onNext() {
        val state = currentState
        when (state.step) {
            RequestStep.Amount -> if (amountKes(state) > 0) updateState { it.copy(step = RequestStep.Details) }
            RequestStep.Details -> if (detailsComplete(state)) updateState { it.copy(step = RequestStep.Review) }
            RequestStep.Review -> Unit
            RequestStep.Sent -> Unit
        }
    }

    private fun onBack() {
        updateState { state ->
            state.copy(
                step = when (state.step) {
                    RequestStep.Amount -> state.step
                    RequestStep.Details -> RequestStep.Amount
                    RequestStep.Review -> RequestStep.Details
                    RequestStep.Sent -> state.step
                },
            )
        }
        if (currentState.step == RequestStep.Amount) sendEvent(RequestCashEvent.NavigateHome)
    }

    private fun onSend() {
        val state = currentState
        if (state.submitting) return
        updateState { it.copy(submitting = true) }
        viewModelScope.launch {
            val request = repository.createRequest(
                amountKes = amountKes(state),
                purpose = state.purpose,
                category = state.category,
                project = state.project,
                site = state.site,
                neededBy = state.neededBy,
                note = state.note,
            )
            updateState { it.copy(submitting = false, step = RequestStep.Sent, sentRequest = request) }
        }
    }

    private fun detailsComplete(state: RequestCashState): Boolean =
        state.purpose.isNotBlank() &&
            state.category.isNotBlank() &&
            state.project.isNotBlank() &&
            state.site.isNotBlank() &&
            state.neededBy.isNotBlank()

    private fun amountKes(state: RequestCashState): Long =
        state.amountDigits.toLongOrNull() ?: 0L

    private companion object {
        const val MAX_DIGITS = 7
    }
}
