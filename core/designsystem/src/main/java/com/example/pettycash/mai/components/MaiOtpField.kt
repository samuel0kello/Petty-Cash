package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiOtpField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val digits = value.filter { it.isDigit() }.take(length)
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    val activeIndex = digits.length.coerceAtMost(length - 1)

    BasicTextField(
        value = digits,
        onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }.take(length)
            if (filtered != digits) onValueChange(filtered)
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Code, $length digits, ${digits.length} entered"
            },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            ) {
                repeat(length) { index ->
                    val isActive = index == digits.length && digits.length < length
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp)
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) MaterialTheme.colorScheme.primary else borderColor,
                                shape = MaiTheme.shapes.md,
                            )
                            .padding(1.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (index < digits.length) {
                            Text(
                                digits[index].toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        },
    )
}
