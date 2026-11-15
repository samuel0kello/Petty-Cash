package com.example.pettycash.mai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

data class MaiRoleOption(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val selected: Boolean = false,
)

@Composable
fun MaiRoleSwitcher(
    roles: List<MaiRoleOption>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val selected = roles.firstOrNull { it.selected } ?: roles.firstOrNull() ?: return

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss)
                .semantics { contentDescription = "Dismiss role switcher" },
        )
        Surface(
            shape = MaiTheme.shapes.lg,
            color = scheme.surfaceContainerHigh,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .width(160.dp)
                .heightIn(min = 48.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = MaiTheme.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    selected.icon,
                    contentDescription = null,
                    tint = scheme.onSurface,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    selected.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Surface(
            shape = MaiTheme.shapes.md,
            color = scheme.surfaceContainerHigh,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 64.dp, end = MaiTheme.spacing.lg)
                .width(240.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = MaiTheme.spacing.sm)) {
                roles.forEachIndexed { index, role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .semantics { contentDescription = "${role.label}, ${role.subtitle}" }
                            .clickable { onSelect(index) }
                            .padding(horizontal = MaiTheme.spacing.lg, vertical = MaiTheme.spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            role.icon,
                            contentDescription = null,
                            tint = scheme.onSurface,
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                role.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = scheme.onSurface,
                            )
                            Text(
                                role.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
