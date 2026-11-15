package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiBannerTone { Info, Success, Warning, Error }

@Composable
fun MaiBanner(
    message: String,
    tone: MaiBannerTone,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val ext = MaiTheme.extendedColors
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (tone) {
        MaiBannerTone.Info -> ext.infoContainer to ext.onInfoContainer
        MaiBannerTone.Success -> ext.successContainer to ext.onSuccessContainer
        MaiBannerTone.Warning -> ext.warningContainer to ext.onWarningContainer
        MaiBannerTone.Error -> scheme.errorContainer to scheme.onErrorContainer
    }
    Surface(
        modifier = modifier.fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = MaiTheme.shapes.md,
        color = container,
        contentColor = content,
    ) {
        Row(
            modifier = Modifier.padding(start = MaiTheme.spacing.lg, top = MaiTheme.spacing.lg, end = MaiTheme.spacing.sm, bottom = MaiTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = content,
                modifier = Modifier.weight(1f),
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(actionLabel, style = MaterialTheme.typography.labelLarge, color = content)
                }
            }
        }
    }
}
