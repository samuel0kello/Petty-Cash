package com.example.pettycash.features.invite.impl.ui.invite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.invite.impl.R
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiSwitchRow
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InviteFormRoute(
    onInviteFormEvent: (InviteFormEvent) -> Unit,
    viewModel: InviteFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = InviteFormState())

    CollectOneTimeEvent(viewModel.event) { event -> onInviteFormEvent(event) }

    InviteFormScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun InviteFormScreen(
    state: InviteFormState,
    onAction: (InviteFormAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.invite_people_title),
                onNavigationClick = { onAction(InviteFormAction.OnBack) },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
                MaiTextField(
                    value = state.name,
                    onValueChange = { onAction(InviteFormAction.OnNameChange(it)) },
                    label = stringResource(R.string.invite_field_name),
                )
                MaiTextField(
                    value = state.phone,
                    onValueChange = { onAction(InviteFormAction.OnPhoneChange(it)) },
                    label = stringResource(R.string.invite_field_phone),
                    prefix = "+254",
                    keyboardType = KeyboardType.Phone,
                )
                Text(
                    stringResource(R.string.invite_role_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Column {
                    MaiSwitchRow(
                        title = stringResource(R.string.invite_role_approver),
                        supportingText = stringResource(R.string.invite_role_approver_hint),
                        checked = state.approver,
                        onCheckedChange = { onAction(InviteFormAction.OnApproverChange(it)) },
                    )
                    MaiSwitchRow(
                        title = stringResource(R.string.invite_role_custodian),
                        supportingText = stringResource(R.string.invite_role_custodian_hint),
                        checked = state.custodian,
                        onCheckedChange = { onAction(InviteFormAction.OnCustodianChange(it)) },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            ) {
                MaiButton(
                    text = stringResource(R.string.invite_copy_link),
                    onClick = { onAction(InviteFormAction.OnCopyLink) },
                    type = MaiButtonType.Text,
                    modifier = Modifier.weight(1f),
                )
                MaiButton(
                    text = stringResource(R.string.invite_send),
                    onClick = { onAction(InviteFormAction.OnSend) },
                    enabled = state.name.isNotBlank() && state.phone.isNotBlank(),
                    isLoading = state.isSending,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
