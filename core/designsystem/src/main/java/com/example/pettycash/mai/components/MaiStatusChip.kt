package com.example.pettycash.mai.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiStatus {
    Paid, Approved, Pending, NeedsInfo, Rejected, NoReceipt, ReceiptOverdue, WaitingToSend, Confirming;

    val label: String
        get() = when (this) {
            Paid -> "Paid"
            Approved -> "Approved"
            Pending -> "Pending"
            NeedsInfo -> "Needs info"
            Rejected -> "Rejected"
            NoReceipt -> "No receipt"
            ReceiptOverdue -> "Receipt overdue"
            WaitingToSend -> "Waiting to send"
            Confirming -> "Confirming"
        }
}

private data class StatusColors(val container: androidx.compose.ui.graphics.Color, val content: androidx.compose.ui.graphics.Color)

@Composable
private fun MaiStatus.colors(): StatusColors {
    val ext = MaiTheme.extendedColors
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        MaiStatus.Paid, MaiStatus.Approved -> StatusColors(ext.successContainer, ext.onSuccessContainer)
        MaiStatus.Pending, MaiStatus.Confirming -> StatusColors(ext.infoContainer, ext.onInfoContainer)
        MaiStatus.NeedsInfo, MaiStatus.NoReceipt -> StatusColors(ext.warningContainer, ext.onWarningContainer)
        MaiStatus.Rejected, MaiStatus.ReceiptOverdue -> StatusColors(scheme.errorContainer, scheme.onErrorContainer)
        MaiStatus.WaitingToSend -> StatusColors(scheme.surfaceContainerHighest, scheme.onSurfaceVariant)
    }
}

fun MaiStatus.icon(): ImageVector = when (this) {
    MaiStatus.Paid -> Icons.Filled.CheckCircle
    MaiStatus.Approved -> Icons.Filled.Verified
    MaiStatus.Pending -> Icons.Filled.Schedule
    MaiStatus.NeedsInfo -> Icons.AutoMirrored.Filled.Help
    MaiStatus.Rejected -> Icons.Filled.Cancel
    MaiStatus.NoReceipt -> Icons.AutoMirrored.Filled.ReceiptLong
    MaiStatus.ReceiptOverdue -> Icons.Filled.Error
    MaiStatus.WaitingToSend -> Icons.Filled.CloudOff
    MaiStatus.Confirming -> Icons.Filled.Sync
}

@Composable
fun MaiStatusChip(
    status: MaiStatus,
    modifier: Modifier = Modifier,
) {
    val colors = status.colors()
    Surface(
        modifier = modifier.height(24.dp),
        shape = MaiTheme.shapes.sm,
        color = colors.container,
        contentColor = colors.content,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = MaiTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(status.icon(), contentDescription = null, modifier = Modifier.size(16.dp))
            Text(status.label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
