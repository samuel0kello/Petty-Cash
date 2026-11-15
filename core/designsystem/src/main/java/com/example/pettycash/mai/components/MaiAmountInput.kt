package com.example.pettycash.mai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun MaiAmountInput(
    amountDigits: String,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    supportingText: String? = null,
    isWarning: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            Text(
                text = "KES",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatAmount(amountDigits),
                style = MaiTheme.customTypography.amountLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics {
                    contentDescription = "Amount, KES ${formatAmount(amountDigits)}"
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 2.dp)
                .background(
                    if (isWarning) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                ),
        )

        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWarning) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("00", "0", "backspace"),
            )
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 64.dp)
                                .clickable {
                                    if (key == "backspace") onBackspace() else onKey(key)
                                }
                                .semantics {
                                    contentDescription = if (key == "backspace") "Delete" else "Key $key"
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (key == "backspace") {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(28.dp),
                                )
                            } else {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatAmount(digits: String): String {
    if (digits.isEmpty()) return "0"
    val value = digits.toLongOrNull() ?: return digits
    return value.toString().reversed().chunked(3).joinToString(",").reversed()
}
