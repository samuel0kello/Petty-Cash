package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    onNavigationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (navigationIcon != null) {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            navigationIcon,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
