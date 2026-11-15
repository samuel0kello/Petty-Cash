package com.example.pettycash.features.reconciliation.impl.ui.reconciliation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.reconciliation.impl.R
import com.example.pettycash.mai.components.MaiAmountInput
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.MaiTransactionRow
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

private val DENOMINATIONS = listOf(1000, 500, 200, 100, 50, 20)

@Composable
internal fun ReconciliationRoute(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: ReconciliationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ReconciliationState())
    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            ReconciliationEvent.Back -> onBack()
            ReconciliationEvent.Done -> onDone()
        }
    }
    ReconciliationScreen(state = state, onAction = viewModel::onAction, onBack = onBack, onDone = onDone)
}

@Composable
internal fun ReconciliationScreen(
    state: ReconciliationState,
    onAction: (ReconciliationAction) -> Unit,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val title = when (state.step) {
        ReconStep.Count, ReconStep.Helper -> R.string.recon_title
        ReconStep.Result -> R.string.recon_result_title
        ReconStep.Explain -> R.string.recon_explain_title
        ReconStep.Submitted -> R.string.recon_submitted_title
        ReconStep.AdminReview -> R.string.recon_review_title
        ReconStep.MonthClosed -> R.string.recon_closed_title
    }
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(
            title = stringResource(title),
            onNavigationClick = onBack,
        )
        when (state.step) {
            ReconStep.Count -> CountStep(state, onAction)
            ReconStep.Helper -> HelperStep(state, onAction)
            ReconStep.Result -> ResultStep(state, onAction)
            ReconStep.Explain -> ExplainStep(state, onAction)
            ReconStep.Submitted -> SubmittedStep(state, onAction, onDone)
            ReconStep.AdminReview -> AdminReviewStep(state, onAction)
            ReconStep.MonthClosed -> MonthClosedStep(state, onAction, onDone)
        }
    }
}

@Composable
private fun ColumnScope.CountStep(state: ReconciliationState, onAction: (ReconciliationAction) -> Unit) {
    val variance = state.countedKes - (state.snapshot?.context?.expectedKes ?: 0L)
    Column(modifier = Modifier.weight(1f)) {
        MaiAmountInput(
            amountDigits = state.amountDigits,
            onKey = { onAction(ReconciliationAction.Digit(it)) },
            onBackspace = { onAction(ReconciliationAction.Backspace) },
            label = stringResource(R.string.recon_count_label),
            supportingText = stringResource(R.string.recon_count_hint),
            isWarning = state.error != null,
            modifier = Modifier.weight(1f),
        )
    }
    Column(
        modifier = Modifier.padding(
            horizontal = MaiTheme.spacing.xxl,
            vertical = MaiTheme.spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        MaiButton(
            text = stringResource(R.string.recon_helper),
            onClick = { onAction(ReconciliationAction.ToggleHelper) },
            type = MaiButtonType.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )
        MaiButton(
            text = stringResource(R.string.recon_next),
            onClick = { onAction(ReconciliationAction.Next) },
            enabled = state.countedKes > 0,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColumnScope.HelperStep(state: ReconciliationState, onAction: (ReconciliationAction) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        DENOMINATIONS.forEach { kes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "KES ${formatAmount(kes.toString())}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MaiTextField(
                    value = state.denominationKes[kes] ?: "",
                    onValueChange = { onAction(ReconciliationAction.Denomination(kes, it)) },
                    modifier = Modifier.weight(0.4f),
                )
            }
        }
        Text(
            stringResource(R.string.recon_helper_total, formatAmount(state.helperTotal.toString())),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
    Column(
        modifier = Modifier.padding(
            horizontal = MaiTheme.spacing.xxl,
            vertical = MaiTheme.spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        MaiButton(
            text = stringResource(R.string.recon_use_total),
            onClick = { onAction(ReconciliationAction.UseHelperTotal) },
            modifier = Modifier.fillMaxWidth(),
        )
        MaiButton(
            text = stringResource(R.string.recon_next),
            onClick = { onAction(ReconciliationAction.Next) },
            enabled = state.countedKes > 0,
            type = MaiButtonType.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColumnScope.ResultStep(state: ReconciliationState, onAction: (ReconciliationAction) -> Unit) {
    val record = state.snapshot?.record
    val counted = state.countedKes
    val expected = state.snapshot?.context?.expectedKes ?: 0L
    val variance = counted - expected
    val balanced = variance == 0L
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    ) {
        MaiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            ) {
                SummaryRow(stringResource(R.string.recon_counted), formatAmount(counted.toString()))
                SummaryRow(stringResource(R.string.recon_expected), formatAmount(expected.toString()))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.recon_variance),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
                        if (!balanced) {
                            Text(
                                if (variance < 0) "−${formatAmount((-variance).toString())}"
                                else "+${formatAmount(variance.toString())}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                stringResource(if (variance < 0) R.string.recon_short else R.string.recon_over),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            MaiStatusChip(MaiStatus.Balanced)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
    MaiButton(
        text = stringResource(if (balanced) R.string.recon_submit else R.string.generic_next),
        onClick = {
            if (balanced) onAction(ReconciliationAction.Submit)
            else onAction(ReconciliationAction.ToggleHelper).let { }
        },
        enabled = !state.submitting,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "KES $value",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ColumnScope.ExplainStep(state: ReconciliationState, onAction: (ReconciliationAction) -> Unit) {
    val unreceipted = state.snapshot?.context?.unreceiptedSpends.orEmpty()
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        if (unreceipted.isNotEmpty()) {
            Text(
                stringResource(R.string.recon_unreceipted, unreceipted.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            unreceipted.forEach { spend ->
                MaiTransactionRow(
                    title = spend.description,
                    meta = stringResource(R.string.recon_no_receipt),
                    amountKes = spend.amountKes,
                    direction = com.example.pettycash.mai.components.MaiRowDirection.Out,
                    status = MaiStatus.NoReceipt,
                )
            }
        }
        MaiTextField(
            value = state.explanation,
            onValueChange = { onAction(ReconciliationAction.ExplanationChanged(it)) },
            label = stringResource(R.string.recon_explanation_hint),
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
    MaiButton(
        text = stringResource(R.string.recon_submit),
        onClick = { onAction(ReconciliationAction.Submit) },
        enabled = !state.submitting,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.SubmittedStep(
    state: ReconciliationState,
    onAction: (ReconciliationAction) -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.recon_submitted_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            stringResource(R.string.recon_submitted_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaiTheme.spacing.md),
        )
        Spacer(Modifier.height(MaiTheme.spacing.xl))
        MaiButton(text = stringResource(R.string.recon_done), onClick = onDone)
    }
}

@Composable
private fun ColumnScope.AdminReviewStep(state: ReconciliationState, onAction: (ReconciliationAction) -> Unit) {
    val record = state.snapshot?.record
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        if (record != null) {
            MaiCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(MaiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    SummaryRow("Counted", formatAmount(record.countedKes.toString()))
                    SummaryRow("Expected", formatAmount(record.expectedKes.toString()))
                    SummaryRow("Difference", formatAmount(record.varianceKes.toString()))
                    if (record.explanation.isNotBlank()) {
                        Text(
                            record.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
    MaiButton(
        text = stringResource(R.string.recon_close_month),
        onClick = { onAction(ReconciliationAction.CloseMonth) },
        enabled = !state.submitting,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.MonthClosedStep(
    state: ReconciliationState,
    onAction: (ReconciliationAction) -> Unit,
    onDone: () -> Unit,
) {
    val record = state.snapshot?.record
    Column(
        modifier = Modifier.fillMaxSize().padding(MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.recon_closed_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            stringResource(
                R.string.recon_closed_body,
                record?.month ?: "",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaiTheme.spacing.md),
        )
        Spacer(Modifier.height(MaiTheme.spacing.xl))
        MaiButton(text = stringResource(R.string.recon_done), onClick = onDone)
    }
}
