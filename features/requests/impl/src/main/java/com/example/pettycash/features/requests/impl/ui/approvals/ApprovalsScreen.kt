package com.example.pettycash.features.requests.impl.ui.approvals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.requests.impl.R
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiEmptyState
import com.example.pettycash.mai.components.MaiNavItem
import com.example.pettycash.mai.components.MaiNavigationBar
import com.example.pettycash.mai.components.MaiSegmentedButton
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ApprovalsRoute(
    onApprovalsEvent: (ApprovalsEvent) -> Unit,
    viewModel: ApprovalsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ApprovalsState())
    CollectOneTimeEvent(viewModel.event) { onApprovalsEvent(it) }
    ApprovalsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ApprovalsScreen(
    state: ApprovalsState,
    onAction: (ApprovalsAction) -> Unit,
) {
    val requests = requestsForTab(state, state.tab)
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.approvals_title),
                onNavigationClick = {},
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.sm),
            ) {
                MaiSegmentedButton(
                    options = ApprovalTab.entries,
                    selected = state.tab,
                    onSelect = { onAction(ApprovalsAction.OnTabSelect(it)) },
                    label = {
                        stringResource(
                            when (it) {
                                ApprovalTab.Waiting -> R.string.approvals_tab_waiting
                                ApprovalTab.Approved -> R.string.approvals_tab_approved
                                ApprovalTab.Rejected -> R.string.approvals_tab_rejected
                            },
                        )
                    },
                )
            }
            if (state.tab == ApprovalTab.Waiting) {
                Text(
                    stringResource(
                        R.string.approvals_total_waiting,
                        formatAmount(waitingTotalKes(state).toString()),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = MaiTheme.spacing.xxl,
                        vertical = MaiTheme.spacing.md,
                    ),
                )
            }
            if (requests.isEmpty() && state.loaded) {
                MaiEmptyState(
                    icon = Icons.Filled.TaskAlt,
                    title = stringResource(
                        when (state.tab) {
                            ApprovalTab.Waiting -> R.string.approvals_empty_waiting
                            ApprovalTab.Approved -> R.string.approvals_empty_approved
                            ApprovalTab.Rejected -> R.string.approvals_empty_rejected
                        },
                    ),
                    message = stringResource(R.string.approvals_empty_message),
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = MaiTheme.spacing.xxl,
                        vertical = MaiTheme.spacing.sm,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    items(requests, key = { it.id }) { request ->
                        ApprovalCard(request) { onAction(ApprovalsAction.OnRequestClick(request.id)) }
                    }
                }
            }
            MaiNavigationBar(
                items = listOf(
                    MaiNavItem(stringResource(R.string.nav_home), Icons.Outlined.Home, selectedIcon = Icons.Filled.Home),
                    MaiNavItem(stringResource(R.string.nav_approvals), Icons.Filled.TaskAlt),
                    MaiNavItem(stringResource(R.string.nav_float), Icons.Filled.AccountBalanceWallet),
                    MaiNavItem(stringResource(R.string.nav_people), Icons.Filled.Group),
                    MaiNavItem(stringResource(R.string.nav_more), Icons.Filled.MoreHoriz),
                ),
                selectedIndex = 1,
                onSelect = { onAction(ApprovalsAction.OnNavSelect(it)) },
            )
        }
    }
}

@Composable
private fun ApprovalCard(request: CashRequest, onClick: () -> Unit) {
    MaiCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    request.requesterName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                MaiStatusChip(status = statusFor(request.status))
            }
            Text(
                request.purpose,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${request.voucherCode} · ${request.category} · needed ${request.neededBy}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "KES ${formatAmount(request.amountKes.toString())}",
                    style = MaiTheme.customTypography.amountMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

private fun statusFor(status: String): MaiStatus = when (status) {
    RequestStatus.PENDING -> MaiStatus.Pending
    RequestStatus.INFO_REQUESTED -> MaiStatus.NeedsInfo
    RequestStatus.APPROVED -> MaiStatus.Approved
    RequestStatus.REJECTED -> MaiStatus.Rejected
    RequestStatus.PAID -> MaiStatus.Paid
    else -> MaiStatus.Pending
}
