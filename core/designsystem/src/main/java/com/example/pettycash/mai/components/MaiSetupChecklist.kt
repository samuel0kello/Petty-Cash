package com.example.pettycash.mai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.pettycash.mai.theme.MaiTheme

data class MaiSetupStep(
    val label: String,
    val done: Boolean,
    val actionable: Boolean = false,
)

@Composable
fun MaiSetupChecklist(
    title: String,
    doneCount: Int,
    totalCount: Int,
    steps: List<MaiSetupStep>,
    modifier: Modifier = Modifier,
    onStepClick: ((Int) -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaiTheme.shapes.lg,
        color = scheme.primaryContainer,
        contentColor = scheme.onPrimaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "$doneCount of $totalCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onPrimaryContainer,
                )
            }
            LinearProgressIndicator(
                progress = { if (totalCount > 0) doneCount.toFloat() / totalCount else 0f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = scheme.primary,
                trackColor = scheme.surface,
            )
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (step.actionable && onStepClick != null) Modifier.clickable { onStepClick(index) } else Modifier)
                        .semantics { contentDescription = step.label },
                    horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (step.done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = scheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        step.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f),
                    )
                    if (step.actionable) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = scheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Spacer(Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
