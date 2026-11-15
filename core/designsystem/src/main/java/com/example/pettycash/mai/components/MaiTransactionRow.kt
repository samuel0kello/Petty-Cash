package com.example.pettycash.mai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiRowDirection { Out, In }

@Composable
fun MaiTransactionRow(
    title: String,
    meta: String,
    amountKes: Long,
    direction: MaiRowDirection,
    status: MaiStatus,
    modifier: Modifier = Modifier,
    showAmountSign: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val sign = if (direction == MaiRowDirection.Out) "−" else "+"
    val amountText = if (showAmountSign) "$sign ${formatKes(amountKes)}" else formatKes(amountKes)
    val spoken = "$title, $amountText, ${status.label}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { contentDescription = spoken }
            .padding(horizontal = MaiTheme.spacing.lg, vertical = MaiTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            color = scheme.surfaceContainerHighest,
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    when (direction) {
                        MaiRowDirection.Out -> Icons.Filled.ArrowUpward
                        MaiRowDirection.In -> Icons.Filled.ArrowDownward
                    },
                    contentDescription = null,
                    tint = scheme.onSurface,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Spacer(Modifier.width(MaiTheme.spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.heightIn(min = 2.dp))
            Text(
                meta,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(MaiTheme.spacing.sm))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                amountText,
                style = MaiTheme.customTypography.amountSmall,
                color = scheme.onSurface,
            )
            Spacer(Modifier.heightIn(min = MaiTheme.spacing.xs))
            MaiStatusChip(status)
        }
    }
}
