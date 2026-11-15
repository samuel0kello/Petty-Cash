package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    val borderWidth = if (isFocused || isError) 2.dp else 1.dp
    val labelColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.38f)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            ),
            cursorBrush = SolidColor(
                if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(MaiTheme.shapes.xs)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(borderWidth, borderColor, MaiTheme.shapes.xs)
                        .padding(horizontal = MaiTheme.spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            leadingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    prefix?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (label != null) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = labelColor,
                            )
                        }
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            innerTextField()
                        }
                    }
                    trailingIcon?.invoke()
                }
            },
        )
        if (supportingText != null) {
            Text(
                supportingText,
                modifier = Modifier.padding(
                    start = MaiTheme.spacing.lg,
                    top = MaiTheme.spacing.xs,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}
