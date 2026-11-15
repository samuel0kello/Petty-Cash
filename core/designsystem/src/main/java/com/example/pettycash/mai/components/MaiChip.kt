package com.example.pettycash.mai.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiChipVariant { Default, Assist }

@Composable
fun MaiChip(
    label: String,
    modifier: Modifier = Modifier,
    variant: MaiChipVariant = MaiChipVariant.Default,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
    val border = if (selected) {
        null
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { this.selected = selected },
        color = Color.Transparent,
        contentColor = contentColor,
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Row(
                modifier = Modifier
                    .heightIn(min = 32.dp)
                    .clip(MaiTheme.shapes.sm)
                    .background(containerColor)
                    .then(if (border != null) Modifier.border(border, MaiTheme.shapes.sm) else Modifier)
                    .padding(
                        start = if (selected) MaiTheme.spacing.sm else MaiTheme.spacing.lg,
                        end = if (trailingIcon != null) MaiTheme.spacing.sm else MaiTheme.spacing.lg,
                    ),
                horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selected) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                } else if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (trailingIcon != null) {
                    Icon(trailingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
