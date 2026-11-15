package com.example.pettycash.features.requests.impl.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.requests.impl.R
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun RequestDetailRoute(
    requestId: String,
    onRequestDetailEvent: (RequestDetailEvent) -> Unit,
) {
    val viewModel: RequestDetailViewModel = koinViewModel { parametersOf(requestId) }
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = RequestDetailState())
    CollectOneTimeEvent(viewModel.event) { onRequestDetailEvent(it) }
    RequestDetailScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun RequestDetailScreen(
    state: RequestDetailState,
    onAction: (RequestDetailAction) -> Unit,
) {
    val request = state.request
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.request_cash_title),
                onNavigationClick = { onAction(RequestDetailAction.Back) },
            )
            when {
                request == null -> Unit
                request.status == RequestStatus.PAID -> PaidContent(request, onAction)
                else -> PendingContent(request, onAction)
            }
        }
    }
}

@Composable
private fun ColumnScope.PendingContent(request: CashRequest, onAction: (RequestDetailAction) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.xs)) {
            Text(
                "KES ${formatAmount(request.amountKes.toString())}",
                style = MaiTheme.customTypography.amountLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                request.purpose,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MaiStatusChip(status = statusFor(request.status))
        }
        Text(
            stringResource(R.string.request_detail_where),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        TimelineEntry("Request sent", request.voucherCode)
        TimelineEntry("Waiting for ${request.reviewerName} to review")
        if (request.status == RequestStatus.APPROVED) {
            TimelineEntry("Approved · paying by M-Pesa")
        }
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.request_detail_withdraw),
            onClick = { onAction(RequestDetailAction.Withdraw) },
            type = MaiButtonType.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColumnScope.PaidContent(request: CashRequest, onAction: (RequestDetailAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaiTheme.extendedColors.successContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaiTheme.extendedColors.onSuccessContainer,
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(Modifier.size(MaiTheme.spacing.lg))
        Text(
            stringResource(R.string.request_detail_paid_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.size(MaiTheme.spacing.sm))
        Text(
            stringResource(
                R.string.request_detail_paid_body,
                formatAmount(request.amountKes.toString()),
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (request.paidReference.isNotBlank()) {
            Spacer(Modifier.size(MaiTheme.spacing.md))
            Text(
                request.paidReference,
                style = MaiTheme.customTypography.codeMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { contentDescription = "M-Pesa reference ${request.paidReference}" },
            )
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            MaiButton(
                text = stringResource(R.string.request_detail_add_receipt),
                onClick = { onAction(RequestDetailAction.AddReceipt) },
                modifier = Modifier.fillMaxWidth(),
            )
            MaiButton(
                text = stringResource(R.string.request_done),
                onClick = { onAction(RequestDetailAction.Done) },
                type = MaiButtonType.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.size(MaiTheme.spacing.xxl))
    }
}

@Composable
private fun TimelineEntry(title: String, detail: String? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md)) {
        Box(
            modifier = Modifier
                .padding(top = MaiTheme.spacing.xs)
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (detail != null) {
                Text(
                    detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
