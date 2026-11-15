package com.example.pettycash.features.invite.impl.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.invite.impl.R
import com.example.pettycash.features.invite.impl.data.InvitePreviewStatus
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun InvitePreviewRoute(
    inviteId: String,
    onInvitePreviewEvent: (InvitePreviewEvent) -> Unit,
) {
    val viewModel: InvitePreviewViewModel = koinViewModel { parametersOf(inviteId) }
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = InvitePreviewState())

    CollectOneTimeEvent(viewModel.event) { event -> onInvitePreviewEvent(event) }

    InvitePreviewScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun InvitePreviewScreen(
    state: InvitePreviewState,
    onAction: (InvitePreviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val invite = state.invite
            if (state.status == InvitePreviewStatus.Valid && invite != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    Text(
                        stringResource(
                            R.string.invite_preview_sentence,
                            invite.invitedBy.ifBlank { "A teammate" },
                            state.companyName,
                            invite.role,
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = MaiTheme.spacing.xl),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaiTheme.spacing.xxl),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    MaiButton(
                        text = stringResource(R.string.invite_preview_accept),
                        onClick = { onAction(InvitePreviewAction.OnAccept) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MaiButton(
                        text = stringResource(R.string.invite_preview_decline),
                        onClick = { onAction(InvitePreviewAction.OnDecline) },
                        type = MaiButtonType.Text,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    val inviter = invite?.invitedBy?.ifBlank { null } ?: "the admin"
                    Text(
                        when (state.status) {
                            InvitePreviewStatus.Expired ->
                                stringResource(R.string.invite_error_expired_title)
                            else -> stringResource(R.string.invite_error_number_title)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = MaiTheme.spacing.xl),
                    )
                    Text(
                        when (state.status) {
                            InvitePreviewStatus.Expired ->
                                stringResource(R.string.invite_error_expired_body, inviter)
                            else -> stringResource(R.string.invite_error_number_body, inviter)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = MaiTheme.spacing.sm),
                    )
                }
                MaiButton(
                    text = stringResource(R.string.invite_error_back),
                    onClick = { onAction(InvitePreviewAction.OnErrorBack) },
                    type = MaiButtonType.Text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaiTheme.spacing.xxl),
                )
            }
        }
    }
}
