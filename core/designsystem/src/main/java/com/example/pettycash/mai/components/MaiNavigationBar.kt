package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

data class MaiNavItem(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String? = null,
)

@Composable
fun MaiNavigationBar(
    items: List<MaiNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.contentDescription ?: item.label,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
