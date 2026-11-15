package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiPinPadState { Idle, Partial, Error, Locked }

@Composable
fun MaiPinDots(
    entered: Int,
    length: Int = 4,
    state: MaiPinPadState = MaiPinPadState.Idle,
    modifier: Modifier = Modifier,
) {
    val dotColor = when (state) {
        MaiPinPadState.Error -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    val emptyColor = when (state) {
        MaiPinPadState.Error -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    Row(
        modifier = modifier.semantics {
            contentDescription = "$entered of $length digits entered"
        },
        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        repeat(length) { index ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (index < entered) dotColor else Color.Transparent,
                        CircleShape,
                    )
                    .border(2.dp, if (index < entered) dotColor else emptyColor, CircleShape),
            )
        }
    }
}

@Composable
fun MaiPinPad(
    entered: Int,
    onDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4,
    state: MaiPinPadState = MaiPinPadState.Idle,
    title: String? = null,
    message: String? = null,
    enabled: Boolean = true,
    onBiometric: (() -> Unit)? = null,
) {
    val interactive = enabled && state != MaiPinPadState.Locked
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaiTheme.spacing.xxl)
            .alpha(if (state == MaiPinPadState.Locked) 0.38f else 1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.xxl),
    ) {
        if (title != null) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
        MaiPinDots(entered = entered, length = length, state = state)
        if (message != null) {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (state == MaiPinPadState.Error) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center,
            )
        }
        val rows = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
            listOf(null, 0, -1),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { key ->
                        when {
                            key != null && key >= 0 -> PinKey(
                                label = key.toString(),
                                onClick = { onDigit(key) },
                                enabled = interactive,
                                modifier = Modifier.weight(1f),
                            )
                            key == null -> PinKey(
                                label = "Fingerprint",
                                onClick = { onBiometric?.invoke() },
                                enabled = interactive && onBiometric != null,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.Fingerprint,
                            )
                            else -> PinKey(
                                label = "Delete",
                                onClick = onDelete,
                                enabled = interactive && entered > 0,
                                modifier = Modifier.weight(1f),
                                icon = Icons.AutoMirrored.Filled.Backspace,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp),
            )
        } else {
            Text(
                label,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}
