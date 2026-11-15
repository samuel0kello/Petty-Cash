package com.example.pettycash.features.floats.impl.ui.floats

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.floats.impl.data.FloatRepository
import com.example.pettycash.presentation.MviViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FloatsViewModel(
    private val repository: FloatRepository,
) : MviViewModel<FloatsAction, FloatsEvent, FloatsState>(FloatsState()) {

    init {
        repository.snapshot
            .onEach { snapshot -> updateState { it.copy(loading = false, snapshot = snapshot) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: FloatsAction) {
        when (action) {
            is FloatsAction.OpenFloat -> sendEvent(FloatsEvent.OpenFloatDetail(action.state))
        }
    }
}

class FloatDetailViewModel(
    private val repository: FloatRepository,
) : MviViewModel<FloatDetailAction, FloatDetailEvent, FloatDetailState>(
    FloatDetailState(monthLabel = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())),
) {

    init {
        repository.snapshot
            .onEach { snapshot -> updateState { it.copy(loading = false, snapshot = snapshot) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: FloatDetailAction) {
        when (action) {
            FloatDetailAction.TopUp -> sendEvent(FloatDetailEvent.TopUp)
            FloatDetailAction.StartReconciliation -> sendEvent(FloatDetailEvent.StartReconciliation)
        }
    }
}

class TopUpViewModel(
    private val repository: FloatRepository,
) : MviViewModel<TopUpAction, TopUpEvent, TopUpState>(TopUpState()) {

    override fun onAction(action: TopUpAction) {
        when (action) {
            is TopUpAction.Digit -> updateState {
                it.copy(
                    amountDigits = (it.amountDigits + action.key).take(MAX_DIGITS),
                    error = null,
                )
            }
            TopUpAction.Backspace -> updateState {
                it.copy(amountDigits = it.amountDigits.dropLast(1), error = null)
            }
            TopUpAction.Next -> {
                val amount = currentState.amountKes
                if (amount <= 0) {
                    updateState { it.copy(error = "Enter an amount") }
                    return
                }
                updateState { it.copy(saving = true, error = null) }
                viewModelScope.launch {
                    repository.addTopUp(amount)
                    updateState { it.copy(saving = false, doneAmountKes = amount) }
                    sendEvent(TopUpEvent.Saved(amount))
                }
            }
            TopUpAction.Done -> updateState { it.copy(doneAmountKes = null) }
        }
    }

    private companion object {
        const val MAX_DIGITS = 9
    }
}
