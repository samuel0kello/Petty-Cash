package com.example.pettycash.features.auth.impl.confirmpin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.features.auth.impl.data.AuthRepository
import com.example.pettycash.features.auth.impl.shared.PinEntryScaffold
import com.example.pettycash.mai.components.MaiPinPadState
import com.example.pettycash.presentation.CollectOneTimeEvent
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal data class ConfirmPinState(
    val entered: Int = 0,
    val digits: String = "",
    val isError: Boolean = false,
    val isVerifying: Boolean = false,
)

internal sealed interface ConfirmPinAction {
    data class OnDigit(val digit: Int) : ConfirmPinAction
    data object OnDelete : ConfirmPinAction
}

internal sealed interface ConfirmPinEvent {
    data object NavigateToFingerprintOffer : ConfirmPinEvent
}

internal class ConfirmPinViewModel(
    private val authRepository: AuthRepository,
) : MviViewModel<ConfirmPinAction, ConfirmPinEvent, ConfirmPinState>(ConfirmPinState()) {

    override fun onAction(action: ConfirmPinAction) {
        when (action) {
            is ConfirmPinAction.OnDigit -> {
                if (currentState.isVerifying || currentState.digits.length >= 4) return
                val digits = currentState.digits + action.digit
                updateState { it.copy(entered = digits.length, digits = digits, isError = false) }
                if (digits.length == 4) verify(digits)
            }
            ConfirmPinAction.OnDelete -> updateState {
                it.copy(digits = it.digits.dropLast(1), entered = (it.entered - 1).coerceAtLeast(0))
            }
        }
    }

    private fun verify(pin: String) {
        updateState { it.copy(isVerifying = true) }
        viewModelScope.launch {
            val matches = authRepository.verifyPin(pin)
            if (matches) {
                sendEvent(ConfirmPinEvent.NavigateToFingerprintOffer)
            } else {
                updateState {
                    it.copy(entered = 0, digits = "", isError = true, isVerifying = false)
                }
            }
        }
    }
}

@Composable
internal fun ConfirmPinRoute(
    onBack: () -> Unit,
    onNavigateToFingerprintOffer: () -> Unit,
    viewModel: ConfirmPinViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ConfirmPinState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            ConfirmPinEvent.NavigateToFingerprintOffer -> onNavigateToFingerprintOffer()
        }
    }

    ConfirmPinScreen(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
internal fun ConfirmPinScreen(
    state: ConfirmPinState,
    onAction: (ConfirmPinAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PinEntryScaffold(
        title = stringResource(R.string.auth_confirm_pin_title),
        entered = state.entered,
        pinState = if (state.isError) MaiPinPadState.Error else MaiPinPadState.Idle,
        message = if (state.isError) stringResource(R.string.auth_pin_mismatch) else stringResource(R.string.auth_confirm_pin_message),
        onBack = onBack,
        onDigit = { onAction(ConfirmPinAction.OnDigit(it)) },
        onDelete = { onAction(ConfirmPinAction.OnDelete) },
        modifier = modifier,
    )
}
