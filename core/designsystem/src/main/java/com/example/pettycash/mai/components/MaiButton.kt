package com.example.pettycash.mai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

enum class MaiButtonType { Filled, Tonal, Outlined, Text }
enum class MaiButtonSize(val height: androidx.compose.ui.unit.Dp) {
    Large(56.dp),
    Medium(48.dp),
}

@Composable
fun MaiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: MaiButtonType = MaiButtonType.Filled,
    size: MaiButtonSize = MaiButtonSize.Large,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val clickable = enabled && !isLoading
    val label: @Composable () -> Unit = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current,
                )
            } else {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                Text(text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    when (type) {
        MaiButtonType.Filled -> Button(
            onClick = onClick,
            modifier = modifier.height(size.height),
            enabled = clickable,
            shape = MaiTheme.shapes.full,
            colors = ButtonDefaults.buttonColors(),
            contentPadding = ButtonDefaults.ContentPadding,
        ) { label() }
        MaiButtonType.Tonal -> Button(
            onClick = onClick,
            modifier = modifier.height(size.height),
            enabled = clickable,
            shape = MaiTheme.shapes.full,
            colors = ButtonDefaults.filledTonalButtonColors(),
            contentPadding = ButtonDefaults.ContentPadding,
        ) { label() }
        MaiButtonType.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(size.height),
            enabled = clickable,
            shape = MaiTheme.shapes.full,
            contentPadding = ButtonDefaults.ContentPadding,
        ) { label() }
        MaiButtonType.Text -> TextButton(
            onClick = onClick,
            modifier = modifier.height(size.height),
            enabled = clickable,
            shape = MaiTheme.shapes.full,
            contentPadding = ButtonDefaults.TextButtonContentPadding,
        ) { label() }
    }
}
