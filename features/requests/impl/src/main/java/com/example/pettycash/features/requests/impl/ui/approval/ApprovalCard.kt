package com.example.pettycash.features.requests.impl.ui.approval

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.features.requests.impl.R
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import java.util.concurrent.TimeUnit

@Composable
fun ApprovalRequestCard(
    request: CashRequest,
    limitKes: Long,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    onApprove: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaiTheme.shapes.lg),
        shape = MaiTheme.shapes.lg,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            RequesterRow(request)
            Text(
                "KES ${formatAmount(request.amountKes.toString())}",
                style = MaiTheme.customTypography.amountMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "${request.purpose}. ${request.project}, ${request.site}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            AutomaticChecks(request, limitKes)
            if (showActions) {
                when (request.status) {
                    RequestStatus.PENDING, RequestStatus.INFO_REQUESTED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                        ) {
                            MaiButton(
                                text = stringResource(R.string.approval_reject),
                                onClick = { onReject?.invoke() },
                                type = MaiButtonType.Text,
                                modifier = Modifier.weight(1f),
                            )
                            MaiButton(
                                text = stringResource(R.string.approval_approve),
                                onClick = { onApprove?.invoke() },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    RequestStatus.APPROVED -> {
                        MaiButton(
                            text = stringResource(R.string.approval_review_payout),
                            onClick = { onApprove?.invoke() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequesterRow(request: CashRequest) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                initials(request.requesterName),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                request.requesterName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${request.kind} · ${timeAgo(request.createdAt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        MaiStatusChip(status = approvalStatus(request.status))
    }
}

@Composable
private fun AutomaticChecks(request: CashRequest, limitKes: Long) {
    val checks = buildList {
        add(
            if (request.kind == KIND_REFUND) {
                stringResource(R.string.approval_check_receipt_attached)
            } else {
                stringResource(R.string.approval_check_receipt_not_needed)
            },
        )
        if (request.project.isNotBlank()) add(stringResource(R.string.approval_check_project_open))
        if (request.amountKes <= limitKes) {
            add(stringResource(R.string.approval_check_within_limit, formatAmount(limitKes.toString())))
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaiTheme.shapes.md,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            Text(
                stringResource(R.string.approval_checks_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            checks.forEach { check ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        check,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

fun initials(name: String): String =
    name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }

fun timeAgo(createdAtMillis: Long): String {
    if (createdAtMillis <= 0L) return "just now"
    val elapsed = System.currentTimeMillis() - createdAtMillis
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        else -> {
            val hours = TimeUnit.MILLISECONDS.toHours(elapsed)
            if (hours < 24) "$hours hours ago" else "${TimeUnit.MILLISECONDS.toDays(elapsed)} days ago"
        }
    }
}

fun approvalStatus(status: String): MaiStatus = when (status) {
    RequestStatus.PENDING -> MaiStatus.Pending
    RequestStatus.INFO_REQUESTED -> MaiStatus.NeedsInfo
    RequestStatus.APPROVED -> MaiStatus.Approved
    RequestStatus.REJECTED -> MaiStatus.Rejected
    RequestStatus.PAID -> MaiStatus.Paid
    else -> MaiStatus.Pending
}

private const val KIND_REFUND = "Refund claim"
