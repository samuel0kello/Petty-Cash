package com.example.pettycash.features.invite.impl.ui.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.invite.impl.R
import com.example.pettycash.mai.components.MaiEmptyState
import com.example.pettycash.mai.components.MaiExtendedFab
import com.example.pettycash.mai.components.MaiListItem
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TeamRoute(
    onTeamEvent: (TeamEvent) -> Unit,
    viewModel: TeamViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TeamState())

    CollectOneTimeEvent(viewModel.event) { event -> onTeamEvent(event) }

    TeamScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun TeamScreen(
    state: TeamState,
    onAction: (TeamAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxSize()) {
                MaiTopAppBar(
                    title = state.companyName.ifBlank { stringResource(R.string.invite_team_title) },
                    onNavigationClick = { onAction(TeamAction.OnBack) },
                )
                if (state.loaded && state.members.size <= 1) {
                    MaiEmptyState(
                        icon = Icons.Filled.PersonAdd,
                        title = stringResource(R.string.invite_empty_title),
                        message = stringResource(R.string.invite_empty_message),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaiTheme.spacing.xxl),
                        action = {
                            MaiExtendedFab(
                                label = stringResource(R.string.invite_empty_action),
                                icon = Icons.Filled.Add,
                                onClick = { onAction(TeamAction.OnInvitePerson) },
                            )
                        },
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = MaiTheme.spacing.xxl),
                    ) {
                        state.members.forEach { member ->
                            val initials = member.name.split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .joinToString("") { it.first().uppercase() }
                            MaiListItem(
                                title = member.name,
                                subtitle = member.role + if (member.isSelf) " · You" else "",
                                avatarInitials = initials,
                                onClick = { onAction(TeamAction.OnMemberClick(member.name)) },
                            )
                        }
                        if (state.invites.isNotEmpty()) {
                            Text(
                                stringResource(R.string.invite_pending_heading, state.invites.size),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(
                                    start = MaiTheme.spacing.xxl,
                                    end = MaiTheme.spacing.xxl,
                                    top = MaiTheme.spacing.lg,
                                    bottom = MaiTheme.spacing.xs,
                                ),
                            )
                            state.invites.forEach { invite ->
                                MaiListItem(
                                    title = invite.name,
                                    subtitle = "${invite.phone} · ${invite.role}",
                                    leadingIcon = Icons.Filled.PersonAdd,
                                    onClick = { onAction(TeamAction.OnInviteClick(invite.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.lg),
        ) {
            MaiExtendedFab(
                label = stringResource(R.string.invite_fab),
                icon = Icons.Filled.Add,
                onClick = { onAction(TeamAction.OnInvitePerson) },
            )
        }
    }
}
