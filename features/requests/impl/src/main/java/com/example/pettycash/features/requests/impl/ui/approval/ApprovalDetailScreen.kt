package com.example.pettycash.features.requests.impl.ui.approval

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiChip
import com.example.pettycash.mai.components.MaiPinDots
import com.example.pettycash.mai.components.MaiPinPad
import com.example.pettycash.mai.components.MaiPinPadState
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun ApprovalDetailRoute(
    requestId: String,
    onApprovalDetailEvent: (ApprovalDetailEvent) -> Unit,
) {
    val viewModel: ApprovalDetailViewModel = koinViewModel { parametersOf(requestId) }
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ApprovalDetailState())
    CollectOneTimeEvent(viewModel.event) { onApprovalDetailEvent(it) }
    ApprovalDetailScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ApprovalDetailScreen(
    state: ApprovalDetailState,
    onAction: (ApprovalDetailAction) -> Unit,
) {
    val request = state.request
    Box(modifier = Modifier.fillMaxSize()) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.approval_detail_title),
                onNavigationClick = { onAction(ApprovalDetailAction.Back) },
            )
            when (state.sheet) {
                ApprovalSheet.PinEntry -> PinStep(state, onAction)
                else -> DetailContent(state, onAction)
            }
        }
    }
    if (state.duplicateVisible) {
        DuplicateDialog(state, onAction)
    }
    when (state.sheet) {
        ApprovalSheet.AskInfo -> AskInfoSheet(state, onAction)
        ApprovalSheet.Reject -> RejectSheet(state, onAction)
        ApprovalSheet.PayoutReview -> PayoutReviewSheet(state, onAction)
        ApprovalSheet.Waiting -> WaitingSheet(state, onAction)
        ApprovalSheet.Paid -> PaidSheet(state, onAction)
        ApprovalSheet.Failed -> FailedSheet(state, onAction)
        else -> Unit
    }
    if (state.rejectedSnackbar) {
        Box(Modifier.align(Alignment.BottomCenter)) { RejectedSnackbar(onAction) }
    }
    }
}

@Composable
private fun ColumnScope.DetailContent(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    val request = state.request ?: return
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    ) {
        ApprovalRequestCard(
            request = request,
            limitKes = state.limitKes,
            onApprove = { onAction(ApprovalDetailAction.Approve) },
            onReject = { onAction(ApprovalDetailAction.Reject) },
        )
        if (request.status == RequestStatus.PENDING || request.status == RequestStatus.INFO_REQUESTED) {
            MaiButton(
                text = stringResource(R.string.approval_ask_info),
                onClick = { onAction(ApprovalDetailAction.AskInfo) },
                type = MaiButtonType.Tonal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaiTheme.spacing.md),
            )
        }
        if (request.reviewNote.isNotBlank() && request.status != RequestStatus.PENDING) {
            Text(
                request.reviewNote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaiTheme.spacing.md),
            )
        }
        Spacer(Modifier.height(MaiTheme.spacing.lg))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun ColumnScope.PinStep(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    val request = state.request
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(
                R.string.approval_pin_title,
                request?.let { "KES ${formatAmount(it.amountKes.toString())}" } ?: "",
            ),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(MaiTheme.spacing.lg))
        MaiPinDots(
            entered = state.pinEntered,
            state = if (state.pinError) MaiPinPadState.Error else MaiPinPadState.Idle,
        )
        if (state.pinError) {
            Text(
                stringResource(R.string.approval_failed_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaiTheme.spacing.sm),
            )
        }
        Spacer(Modifier.weight(1f))
        MaiPinPad(
            entered = state.pinEntered,
            onDigit = { onAction(ApprovalDetailAction.PinDigit(it)) },
            onDelete = { onAction(ApprovalDetailAction.PinDelete) },
            state = if (state.pinError) MaiPinPadState.Error else MaiPinPadState.Idle,
            message = stringResource(R.string.approval_pin_message),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskInfoSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onAction(ApprovalDetailAction.PayoutCancel) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(bottom = MaiTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Text(
                stringResource(R.string.approval_ask_info_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MaiTextField(
                value = state.askText,
                onValueChange = { onAction(ApprovalDetailAction.AskTextChanged(it)) },
                label = stringResource(R.string.approval_ask_info_hint),
            )
            val quick1 = stringResource(R.string.approval_quick_1)
            val quick2 = stringResource(R.string.approval_quick_2)
            Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
                MaiChip(
                    label = quick1,
                    selected = state.askText == quick1,
                    onClick = { onAction(ApprovalDetailAction.AskTextChanged(quick1)) },
                )
                MaiChip(
                    label = quick2,
                    selected = state.askText == quick2,
                    onClick = { onAction(ApprovalDetailAction.AskTextChanged(quick2)) },
                )
            }
            MaiButton(
                text = stringResource(R.string.approval_send),
                onClick = { onAction(ApprovalDetailAction.AskInfoSend) },
                enabled = state.askText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RejectSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onAction(ApprovalDetailAction.RejectDismiss) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(bottom = MaiTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Text(
                stringResource(R.string.approval_reject_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MaiTextField(
                value = state.rejectReason,
                onValueChange = { onAction(ApprovalDetailAction.RejectReasonChanged(it)) },
                label = stringResource(R.string.approval_reject_hint),
            )
            MaiButton(
                text = stringResource(R.string.approval_reject_confirm),
                onClick = { onAction(ApprovalDetailAction.RejectSend) },
                enabled = state.rejectReason.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            MaiButton(
                text = stringResource(R.string.approval_cancel),
                onClick = { onAction(ApprovalDetailAction.RejectDismiss) },
                type = MaiButtonType.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PayoutReviewSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    val request = state.request ?: return
    ModalBottomSheet(onDismissRequest = { onAction(ApprovalDetailAction.PayoutCancel) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(bottom = MaiTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Text(
                stringResource(R.string.approval_review_payout),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MaiCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(MaiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    DetailRow(stringResource(R.string.approval_pay_to), "${request.requesterName} · M-Pesa")
                    DetailRow("${request.voucherCode} · ${request.purpose}", "KES ${formatAmount(request.amountKes.toString())}")
                    DetailRow(stringResource(R.string.approval_fee), "KES ${formatAmount(state.payoutFeeKes.toString())}")
                    DetailRow(
                        stringResource(R.string.approval_total),
                        "KES ${formatAmount((request.amountKes + state.payoutFeeKes).toString())}",
                    )
                }
            }
            MaiButton(
                text = stringResource(R.string.approval_confirm_pay),
                onClick = { onAction(ApprovalDetailAction.PayoutConfirm) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaitingSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    ModalBottomSheet(onDismissRequest = {}, sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            CircularProgressIndicator()
            Text(
                stringResource(R.string.approval_waiting_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(R.string.approval_waiting_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaidSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    val request = state.request
    ModalBottomSheet(onDismissRequest = {}, sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
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
            Text(
                stringResource(R.string.approval_success_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(
                    R.string.approval_success_body,
                    request?.let { formatAmount(it.amountKes.toString()) } ?: "",
                    request?.requesterName ?: "",
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (state.payoutReference.isNotBlank()) {
                Text(
                    state.payoutReference,
                    style = MaiTheme.customTypography.codeMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { contentDescription = "M-Pesa reference ${state.payoutReference}" },
                )
            }
            Spacer(Modifier.height(MaiTheme.spacing.md))
            MaiButton(
                text = stringResource(R.string.request_done),
                onClick = { onAction(ApprovalDetailAction.Done) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FailedSheet(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onAction(ApprovalDetailAction.PayoutCancel) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Icon(
                Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp),
            )
            Text(
                stringResource(R.string.approval_failed_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(R.string.approval_failed_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            MaiButton(
                text = stringResource(R.string.approval_cancel),
                onClick = { onAction(ApprovalDetailAction.PayoutCancel) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DuplicateDialog(state: ApprovalDetailState, onAction: (ApprovalDetailAction) -> Unit) {
    val request = state.request
    AlertDialog(
        onDismissRequest = { onAction(ApprovalDetailAction.DuplicateCancel) },
        title = { Text(stringResource(R.string.approval_duplicate_title)) },
        text = {
            Text(
                stringResource(
                    R.string.approval_duplicate_message,
                    request?.let { formatAmount(it.amountKes.toString()) } ?: "",
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onAction(ApprovalDetailAction.DuplicateCancel) }) {
                Text(stringResource(R.string.approval_cancel))
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(ApprovalDetailAction.DuplicateConfirm) }) {
                Text(stringResource(R.string.approval_send_anyway))
            }
        },
    )
}

@Composable
private fun RejectedSnackbar(onAction: (ApprovalDetailAction) -> Unit) {
    LaunchedEffect(Unit) {
        delay(10_000)
        onAction(ApprovalDetailAction.SnackbarDismiss)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaiTheme.spacing.lg),
        color = MaterialTheme.colorScheme.inverseSurface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = MaiTheme.spacing.lg, vertical = MaiTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${stringResource(R.string.approval_rejected_snackbar)} · ${stringResource(R.string.approval_undo)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            TextButton(onClick = { onAction(ApprovalDetailAction.RejectUndo) }) {
                Text(
                    stringResource(R.string.approval_undo),
                    color = MaterialTheme.colorScheme.inversePrimary,
                )
            }
        }
    }
}
