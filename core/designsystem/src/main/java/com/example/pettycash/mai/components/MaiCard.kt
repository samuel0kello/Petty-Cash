package com.example.pettycash.mai.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = CardDefaults.cardColors(containerColor = MaiTheme.colors.surfaceContainerLow)
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaiTheme.shapes.md,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            shape = MaiTheme.shapes.md,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { content() }
    }
}
