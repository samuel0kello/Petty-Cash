package com.example.pettycash.features.auth.impl.createpin

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

internal data class CreatePinState(
    val entered: Int = 0,
    val digits: String = "",
)

internal sealed interface CreatePinAction {
    data class OnDigit(val digit: Int) : CreatePinAction
    data object OnDelete : CreatePinAction
}

internal sealed interface CreatePinEvent {
    data object NavigateToConfirmPin : CreatePinEvent
}

internal class CreatePinViewModel(
    private val authRepository: AuthRepository,
) : MviViewModel<CreatePinAction, CreatePinEvent, CreatePinState>(CreatePinState()) {

    override fun onAction(action: CreatePinAction) {
        when (action) {
            is CreatePinAction.OnDigit -> {
                if (currentState.digits.length >= 4) return
                val digits = currentState.digits + action.digit
                updateState { it.copy(entered = digits.length, digits = digits) }
                if (digits.length == 4) {
                    viewModelScope.launch {
                        authRepository.savePin(digits)
                        sendEvent(CreatePinEvent.NavigateToConfirmPin)
                    }
                }
            }
            CreatePinAction.OnDelete -> updateState {
                it.copy(digits = it.digits.dropLast(1), entered = (it.entered - 1).coerceAtLeast(0))
            }
        }
    }
}

@Composable
internal fun CreatePinRoute(
    onBack: () -> Unit,
    onNavigateToConfirmPin: () -> Unit,
    viewModel: CreatePinViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = CreatePinState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            CreatePinEvent.NavigateToConfirmPin -> onNavigateToConfirmPin()
        }
    }

    CreatePinScreen(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
internal fun CreatePinScreen(
    state: CreatePinState,
    onAction: (CreatePinAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PinEntryScaffold(
        title = stringResource(R.string.auth_create_pin_title),
        message = stringResource(R.string.auth_create_pin_message),
        entered = state.entered,
        pinState = MaiPinPadState.Idle,
        onBack = onBack,
        onDigit = { onAction(CreatePinAction.OnDigit(it)) },
        onDelete = { onAction(CreatePinAction.OnDelete) },
        modifier = modifier,
    )
}
