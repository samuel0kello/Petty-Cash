package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiFloatState { Healthy, Low, Empty }

@Composable
fun MaiBalanceCard(
    floatName: String,
    balanceKes: Long,
    issuedKes: Long,
    spentKes: Long,
    state: MaiFloatState,
    onTopUp: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val ext = MaiTheme.extendedColors
    val scheme = MaterialTheme.colorScheme
    val (flagBg, flagFg, barColor) = when (state) {
        MaiFloatState.Healthy -> Triple(ext.successContainer, ext.onSuccessContainer, MaterialTheme.colorScheme.primary)
        MaiFloatState.Low -> Triple(ext.warningContainer, ext.onWarningContainer, ext.warning)
        MaiFloatState.Empty -> Triple(scheme.errorContainer, scheme.onErrorContainer, scheme.error)
    }
    val spentFraction = if (issuedKes > 0) (spentKes.toFloat() / issuedKes.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = MaiTheme.shapes.lg,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Column(modifier = Modifier.padding(MaiTheme.spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    floatName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (state != MaiFloatState.Healthy) {
                    Surface(shape = MaiTheme.shapes.sm, color = flagBg, contentColor = flagFg) {
                        Row(
                            modifier = Modifier.padding(horizontal = MaiTheme.spacing.sm, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text(
                                if (state == MaiFloatState.Empty) "Empty" else "Low",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(MaiTheme.spacing.xs))
            Text(
                formatKes(balanceKes),
                style = MaiTheme.customTypography.amountLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(MaiTheme.spacing.md))
            LinearProgressIndicator(
                progress = { spentFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
            Spacer(Modifier.height(MaiTheme.spacing.md))
            Row {
                Text(
                    "Issued ${formatKes(issuedKes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(MaiTheme.spacing.lg))
                Text(
                    "Spent ${formatKes(spentKes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(MaiTheme.spacing.md))
            TextButton(onClick = onTopUp, modifier = Modifier.height(48.dp)) {
                Text("Top up", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

fun formatKes(amount: Long): String =
    "KES " + String.format("%,d", amount)
