package com.example.pettycash.features.reconciliation.impl.ui.reconciliation

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationRepository
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationStatus
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ReconciliationViewModel(
    private val repository: ReconciliationRepository,
) : MviViewModel<ReconciliationAction, ReconciliationEvent, ReconciliationState>(ReconciliationState()) {

    init {
        repository.snapshot
            .onEach { snapshot ->
                updateState { state ->
                    val record = snapshot.record
                    val step = when {
                        snapshot.isAdmin && record?.status == ReconciliationStatus.Submitted.name ->
                            ReconStep.AdminReview
                        snapshot.isAdmin && record?.status == ReconciliationStatus.Closed.name ->
                            ReconStep.MonthClosed
                        record?.status == ReconciliationStatus.Submitted.name ||
                            record?.status == ReconciliationStatus.Closed.name ->
                            ReconStep.Submitted
                        else -> state.step
                    }
                    state.copy(snapshot = snapshot, step = step)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ReconciliationAction) {
        when (action) {
            is ReconciliationAction.Digit -> updateState {
                it.copy(amountDigits = (it.amountDigits + action.key).take(9), error = null)
            }
            ReconciliationAction.Backspace -> updateState {
                it.copy(amountDigits = it.amountDigits.dropLast(1), error = null)
            }
            ReconciliationAction.Next -> {
                val counted = currentState.countedKes
                val expected = currentState.snapshot?.context?.expectedKes
                if (counted <= 0) {
                    updateState { it.copy(error = "Enter the counted amount") }
                    return
                }
                updateState { it.copy(step = ReconStep.Result, error = null) }
            }
            ReconciliationAction.UseHelperTotal -> updateState {
                it.copy(amountDigits = it.helperTotal.toString().take(9))
            }
            ReconciliationAction.ToggleHelper -> updateState {
                it.copy(step = if (it.step == ReconStep.Helper) ReconStep.Count else ReconStep.Helper)
            }
            is ReconciliationAction.Denomination -> updateState {
                it.copy(denominationKes = it.denominationKes + (action.kes to action.value.take(4)))
            }
            is ReconciliationAction.ExplanationChanged -> updateState {
                it.copy(explanation = action.value)
            }
            ReconciliationAction.Explain -> updateState { it.copy(step = ReconStep.Explain) }
            ReconciliationAction.Submit -> {
                val counted = currentState.countedKes
                if (counted <= 0) return
                updateState { it.copy(submitting = true) }
                viewModelScope.launch {
                    val unreceipted = currentState.snapshot?.context?.unreceiptedSpends?.size ?: 0
                    repository.submit(counted, currentState.explanation, unreceipted)
                    updateState { it.copy(submitting = false, step = ReconStep.Submitted) }
                }
            }
            ReconciliationAction.CloseMonth -> {
                updateState { it.copy(submitting = true) }
                viewModelScope.launch {
                    repository.closeMonth()
                    updateState { it.copy(submitting = false, step = ReconStep.MonthClosed) }
                }
            }
            ReconciliationAction.Back -> sendEvent(ReconciliationEvent.Back)
            ReconciliationAction.Done -> sendEvent(ReconciliationEvent.Done)
        }
    }
}
