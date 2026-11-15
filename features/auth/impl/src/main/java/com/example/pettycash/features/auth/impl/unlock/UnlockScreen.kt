package com.example.pettycash.features.auth.impl.unlock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.features.auth.impl.data.AuthRepository
import com.example.pettycash.mai.components.MaiPinPad
import com.example.pettycash.mai.components.MaiPinPadState
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private const val MAX_ATTEMPTS = 5
private const val LOCK_SECONDS = 30

internal data class UnlockState(
    val entered: Int = 0,
    val digits: String = "",
    val isError: Boolean = false,
    val triesLeft: Int = MAX_ATTEMPTS,
    val isLocked: Boolean = false,
    val lockSecondsLeft: Int = 0,
)

internal sealed interface UnlockAction {
    data class OnDigit(val digit: Int) : UnlockAction
    data object OnDelete : UnlockAction
    data object OnForgotPin : UnlockAction
    data object OnBiometric : UnlockAction
}

internal sealed interface UnlockEvent {
    data object NavigateHome : UnlockEvent
    data object NavigateToSmsCode : UnlockEvent
}

internal class UnlockViewModel(
    private val authRepository: AuthRepository,
) : MviViewModel<UnlockAction, UnlockEvent, UnlockState>(UnlockState()) {

    override fun onAction(action: UnlockAction) {
        when (action) {
            is UnlockAction.OnDigit -> {
                if (currentState.isLocked || currentState.digits.length >= 4) return
                val digits = currentState.digits + action.digit
                updateState { it.copy(entered = digits.length, digits = digits, isError = false) }
                if (digits.length == 4) verify(digits)
            }
            UnlockAction.OnDelete -> updateState {
                it.copy(digits = it.digits.dropLast(1), entered = (it.entered - 1).coerceAtLeast(0))
            }
            UnlockAction.OnForgotPin -> {
                authRepository.requestPinReset()
                sendEvent(UnlockEvent.NavigateToSmsCode)
            }
            UnlockAction.OnBiometric -> sendEvent(UnlockEvent.NavigateHome)
        }
    }

    private fun verify(pin: String) {
        viewModelScope.launch {
            val matches = authRepository.verifyPin(pin)
            if (matches) {
                sendEvent(UnlockEvent.NavigateHome)
            } else {
                val triesLeft = (currentState.triesLeft - 1).coerceAtLeast(0)
                if (triesLeft == 0) {
                    updateState {
                        it.copy(
                            entered = 0, digits = "", isError = false,
                            triesLeft = MAX_ATTEMPTS, isLocked = true, lockSecondsLeft = LOCK_SECONDS,
                        )
                    }
                    startLockTimer()
                } else {
                    updateState {
                        it.copy(entered = 0, digits = "", isError = true, triesLeft = triesLeft)
                    }
                }
            }
        }
    }

    private fun startLockTimer() {
        viewModelScope.launch {
            var remaining = LOCK_SECONDS
            while (remaining > 0) {
                delay(1000)
                remaining--
                updateState { it.copy(lockSecondsLeft = remaining) }
            }
            updateState { it.copy(isLocked = false, lockSecondsLeft = 0) }
        }
    }
}

@Composable
internal fun UnlockRoute(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onForgotPin: () -> Unit,
    viewModel: UnlockViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = UnlockState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            UnlockEvent.NavigateHome -> onNavigateHome()
            UnlockEvent.NavigateToSmsCode -> onForgotPin()
        }
    }

    UnlockScreen(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
internal fun UnlockScreen(
    state: UnlockState,
    onAction: (UnlockAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.auth_unlock_initials),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            MaiPinPad(
                entered = state.entered,
                state = when {
                    state.isLocked -> MaiPinPadState.Locked
                    state.isError -> MaiPinPadState.Error
                    else -> MaiPinPadState.Idle
                },
                title = stringResource(R.string.auth_unlock_title),
                message = when {
                    state.isLocked -> "Too many tries. Try again in ${state.lockSecondsLeft}s."
                    state.isError -> stringResource(R.string.auth_unlock_wrong, state.triesLeft)
                    else -> stringResource(R.string.auth_unlock_message)
                },
                onDigit = { onAction(UnlockAction.OnDigit(it)) },
                onDelete = { onAction(UnlockAction.OnDelete) },
                onBiometric = { onAction(UnlockAction.OnBiometric) },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaiTheme.spacing.xxl),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onAction(UnlockAction.OnForgotPin) },
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                ) {
                    Text(
                        stringResource(R.string.auth_forgot_pin),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
