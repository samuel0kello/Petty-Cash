package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onTopUp: (() -> Unit)? = null,
) {
    val ext = MaiTheme.extendedColors
    val scheme = MaterialTheme.colorScheme
    val (flagBg, flagFg, barColor) = when (state) {
        MaiFloatState.Healthy -> Triple(ext.successContainer, ext.onSuccessContainer, scheme.primary)
        MaiFloatState.Low -> Triple(ext.warningContainer, ext.onWarningContainer, ext.warning)
        MaiFloatState.Empty -> Triple(scheme.errorContainer, scheme.onErrorContainer, scheme.error)
    }
    val spentFraction = if (issuedKes > 0) (spentKes.toFloat() / issuedKes.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "$floatName, KES ${formatKes(balanceKes)}. Issued KES ${formatKes(issuedKes)} · Spent KES ${formatKes(spentKes)}"
            },
        shape = MaiTheme.shapes.lg,
        color = scheme.surfaceContainerHighest,
    ) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    floatName,
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurfaceVariant,
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
            Text(
                formatKes(balanceKes),
                style = MaiTheme.customTypography.amountLarge,
                color = scheme.onSurface,
            )
            LinearProgressIndicator(
                progress = { spentFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = barColor,
                trackColor = scheme.surfaceContainer,
            )
            Text(
                "Issued ${formatKes(issuedKes)} · Spent ${formatKes(spentKes)}",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onTopUp != null) {
                    Surface(
                        onClick = onTopUp,
                        shape = CircleShape,
                        color = scheme.secondaryContainer,
                        contentColor = scheme.onSecondaryContainer,
                        modifier = Modifier.height(48.dp),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "Top up",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = MaiTheme.spacing.xxl),
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatKes(amount: Long): String =
    "KES " + String.format("%,d", amount)
