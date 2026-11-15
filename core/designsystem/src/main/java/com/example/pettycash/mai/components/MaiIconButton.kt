package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MaiIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(24.dp))
    }
}
