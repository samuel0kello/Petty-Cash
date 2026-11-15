package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
) {
    Box(
        modifier = modifier
            .size(width = 52.dp, height = 48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .semantics {
                contentDescription = label ?: ""
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 32.dp)
                .clip(CircleShape)
                .background(
                    if (checked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                .then(
                    if (!checked) Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape) else Modifier,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = if (checked) 24.dp else 8.dp)
                    .size(if (checked) 24.dp else 16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            checked -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.outline
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (checked) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun MaiSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(horizontal = MaiTheme.spacing.lg, vertical = MaiTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (supportingText != null) {
                Text(
                    supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        MaiSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            label = title,
        )
    }
}
