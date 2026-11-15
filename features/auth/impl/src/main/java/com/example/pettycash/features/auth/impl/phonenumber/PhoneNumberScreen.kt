package com.example.pettycash.features.auth.impl.phonenumber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.features.auth.impl.shared.AuthTopBar
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PhoneNumberRoute(
    onBack: () -> Unit,
    onNavigateToSmsCode: () -> Unit,
    viewModel: PhoneNumberViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = PhoneNumberState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            PhoneNumberEvent.NavigateToSmsCode -> onNavigateToSmsCode()
            PhoneNumberEvent.ShowSmsPermissionNote -> Unit
        }
    }

    PhoneNumberScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
internal fun PhoneNumberScreen(
    state: PhoneNumberState,
    onAction: (PhoneNumberAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxSize()) {
            AuthTopBar(onBack = onBack, title = stringResource(R.string.auth_get_started))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
            Text(
                stringResource(R.string.auth_phone_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MaiTextField(
                value = state.phone,
                onValueChange = { onAction(PhoneNumberAction.OnPhoneChange(it)) },
                label = stringResource(R.string.auth_phone_field_label),
                prefix = stringResource(R.string.auth_phone_prefix),
                isError = state.isError,
                supportingText = if (state.isError) {
                    stringResource(R.string.auth_phone_invalid)
                } else {
                    stringResource(R.string.auth_phone_hint)
                },
                keyboardType = KeyboardType.Phone,
                enabled = !state.isSubmitting,
                modifier = Modifier.focusRequester(focusRequester),
            )
            Text(
                stringResource(R.string.auth_terms_lead),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
                TextButton(
                    onClick = {},
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                    ),
                ) {
                    Text(
                        stringResource(R.string.auth_terms),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                TextButton(
                    onClick = {},
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                    ),
                ) {
                    Text(
                        stringResource(R.string.auth_privacy),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            MaiButton(
                text = stringResource(R.string.auth_continue),
                onClick = { onAction(PhoneNumberAction.OnContinue) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.phone.length >= 9 && !state.isSubmitting,
                isLoading = state.isSubmitting,
            )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneNumberScreenPreview() {
    MaiTheme {
        PhoneNumberScreen(
            state = PhoneNumberState(phone = "712345678"),
            onAction = {},
            onBack = {},
        )
    }
}
