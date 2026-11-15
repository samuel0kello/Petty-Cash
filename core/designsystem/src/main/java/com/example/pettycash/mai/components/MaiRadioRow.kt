package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaiTheme.shapes.md)
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = selected },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        2.dp,
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    )
                }
            }
        }
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = MaiTheme.spacing.sm),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (supportingText != null) {
                Text(
                    supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
