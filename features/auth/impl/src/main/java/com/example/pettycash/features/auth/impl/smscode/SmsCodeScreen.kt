package com.example.pettycash.features.auth.impl.smscode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.features.auth.impl.shared.AuthTopBar
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiOtpField
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SmsCodeRoute(
    onBack: () -> Unit,
    onNavigateToCreatePin: () -> Unit,
    onNavigateToUnlock: () -> Unit,
    viewModel: SmsCodeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = SmsCodeState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            SmsCodeEvent.NavigateToCreatePin -> onNavigateToCreatePin()
            SmsCodeEvent.NavigateToUnlock -> onNavigateToUnlock()
            SmsCodeEvent.GoBack -> onBack()
        }
    }

    SmsCodeScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
internal fun SmsCodeScreen(
    state: SmsCodeState,
    onAction: (SmsCodeAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxSize()) {
            AuthTopBar(onBack = onBack, title = stringResource(R.string.auth_sms_title))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
                Text(
                    stringResource(R.string.auth_sms_subtitle, state.phoneDisplay),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MaiOtpField(
                    value = state.code,
                    onValueChange = { onAction(SmsCodeAction.OnCodeChange(it)) },
                    isError = state.isError,
                    enabled = !state.isBlocked,
                )
                if (state.isError && state.errorMessage != null) {
                    Text(
                        when (state.errorMessage) {
                            "expired" -> stringResource(R.string.auth_sms_expired)
                            else -> stringResource(R.string.auth_sms_wrong, state.triesLeft)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                if (state.resendInSeconds > 0) {
                    Text(
                        stringResource(
                            R.string.auth_sms_resend,
                            "%d:%02d".format(state.resendInSeconds / 60, state.resendInSeconds % 60),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    TextButton(onClick = { onAction(SmsCodeAction.OnResend) }) {
                        Text(
                            stringResource(R.string.auth_sms_resend_now),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                TextButton(
                    onClick = { onAction(SmsCodeAction.OnChangeNumber) },
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                ) {
                    Text(
                        stringResource(R.string.auth_change_number),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.weight(1f))
                MaiButton(
                    text = stringResource(R.string.auth_continue),
                    onClick = { onAction(SmsCodeAction.OnVerify) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.code.length == 6 && !state.isVerifying && !state.isBlocked,
                    isLoading = state.isVerifying,
                )
            }
        }
    }

    if (state.isBlocked) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.auth_sms_blocked_title, "30 seconds")) },
            text = { Text(stringResource(R.string.auth_sms_blocked_body)) },
            confirmButton = {
                TextButton(onClick = { onAction(SmsCodeAction.OnBlockedDismiss) }) {
                    Text(stringResource(R.string.auth_back))
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SmsCodeScreenPreview() {
    MaiTheme {
        SmsCodeScreen(
            state = SmsCodeState(phoneDisplay = "0712 345 678", code = "123"),
            onAction = {},
            onBack = {},
        )
    }
}
