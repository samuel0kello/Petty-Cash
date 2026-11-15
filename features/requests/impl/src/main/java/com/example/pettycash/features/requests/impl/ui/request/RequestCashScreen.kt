package com.example.pettycash.features.requests.impl.ui.request

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.requests.impl.R
import com.example.pettycash.features.requests.impl.data.RequestContext
import com.example.pettycash.mai.components.MaiAmountInput
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RequestCashRoute(
    onRequestCashEvent: (RequestCashEvent) -> Unit,
    viewModel: RequestCashViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = RequestCashState())
    CollectOneTimeEvent(viewModel.event) { onRequestCashEvent(it) }
    RequestCashScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun RequestCashScreen(
    state: RequestCashState,
    onAction: (RequestCashAction) -> Unit,
) {
    val context = state.context
    val amount = state.amountDigits.toLongOrNull() ?: 0L
    val overLimit = context != null && amount > context.perRequestLimitKes

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.step != RequestStep.Sent) {
            MaiTopAppBar(
                title = stringResource(
                    when (state.step) {
                        RequestStep.Amount -> R.string.request_cash_title
                        RequestStep.Details -> R.string.request_details_title
                        RequestStep.Review -> R.string.request_review_title
                        RequestStep.Sent -> R.string.request_cash_title
                    },
                ),
                onNavigationClick = { onAction(RequestCashAction.Back) },
            )
        }

        when (state.step) {
            RequestStep.Amount -> AmountStep(state, context, overLimit, onAction)
            RequestStep.Details -> DetailsStep(state, context, onAction)
            RequestStep.Review -> ReviewStep(state, context, onAction)
            RequestStep.Sent -> SentStep(state, onAction)
        }
    }
}

@Composable
private fun ColumnScope.AmountStep(
    state: RequestCashState,
    context: RequestContext?,
    overLimit: Boolean,
    onAction: (RequestCashAction) -> Unit,
) {
    Column(modifier = Modifier.weight(1f)) {
        MaiAmountInput(
            amountDigits = state.amountDigits,
            onKey = { onAction(RequestCashAction.AmountKey(it)) },
            onBackspace = { onAction(RequestCashAction.AmountBackspace) },
            label = stringResource(R.string.request_amount_label),
            supportingText = stringResource(
                if (overLimit) R.string.request_amount_over_limit else R.string.request_amount_limit,
                context?.let { formatAmount(it.perRequestLimitKes.toString()) } ?: "5,000",
            ),
            isWarning = overLimit,
            modifier = Modifier.weight(1f),
        )
    }
    MaiButton(
        text = stringResource(R.string.request_next),
        onClick = { onAction(RequestCashAction.Next) },
        enabled = (state.amountDigits.toLongOrNull() ?: 0L) > 0 && !overLimit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.DetailsStep(
    state: RequestCashState,
    context: RequestContext?,
    onAction: (RequestCashAction) -> Unit,
) {
    val reviewer = context?.reviewerName ?: "your admin"
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        MaiTextField(
            value = state.purpose,
            onValueChange = { onAction(RequestCashAction.PurposeChanged(it)) },
            label = stringResource(R.string.request_field_purpose),
            supportingText = stringResource(R.string.request_field_purpose_hint),
        )
        PickerField(
            value = state.category,
            label = stringResource(R.string.request_field_category),
            hint = stringResource(R.string.request_field_category_hint),
            options = context?.categories ?: emptyList(),
            onSelect = { onAction(RequestCashAction.CategoryChanged(it)) },
        )
        if (!context?.projects.isNullOrEmpty()) {
            PickerField(
                value = state.project,
                label = stringResource(R.string.request_field_project),
                hint = stringResource(R.string.request_field_project_hint),
                options = context!!.projects,
                onSelect = { onAction(RequestCashAction.ProjectChanged(it)) },
            )
        } else {
            MaiTextField(
                value = state.project,
                onValueChange = { onAction(RequestCashAction.ProjectChanged(it)) },
                label = stringResource(R.string.request_field_project),
                supportingText = stringResource(R.string.request_field_project_hint),
            )
        }
        if (!context?.sites.isNullOrEmpty()) {
            PickerField(
                value = state.site,
                label = stringResource(R.string.request_field_site),
                hint = stringResource(R.string.request_field_site_hint),
                options = context!!.sites,
                onSelect = { onAction(RequestCashAction.SiteChanged(it)) },
            )
        } else {
            MaiTextField(
                value = state.site,
                onValueChange = { onAction(RequestCashAction.SiteChanged(it)) },
                label = stringResource(R.string.request_field_site),
                supportingText = stringResource(R.string.request_field_site_hint),
            )
        }
        NeededByField(
            value = state.neededBy,
            onSelect = { onAction(RequestCashAction.NeededByChanged(it)) },
        )
        MaiTextField(
            value = state.note,
            onValueChange = { onAction(RequestCashAction.NoteChanged(it)) },
            label = stringResource(R.string.request_field_note),
            supportingText = stringResource(R.string.request_field_note_hint, reviewer),
        )
    }
    MaiButton(
        text = stringResource(R.string.request_next),
        onClick = { onAction(RequestCashAction.Next) },
        enabled = state.purpose.isNotBlank() && state.category.isNotBlank() &&
            state.project.isNotBlank() && state.site.isNotBlank() && state.neededBy.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.ReviewStep(
    state: RequestCashState,
    context: RequestContext?,
    onAction: (RequestCashAction) -> Unit,
) {
    val reviewer = context?.reviewerName ?: "your admin"
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        MaiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            ) {
                SummaryRow(
                    stringResource(R.string.request_review_amount),
                    "KES ${formatAmount(state.amountDigits)}",
                )
                SummaryRow(stringResource(R.string.request_field_purpose), state.purpose)
                SummaryRow(stringResource(R.string.request_field_category), state.category)
                SummaryRow(stringResource(R.string.request_field_project), state.project)
                SummaryRow(stringResource(R.string.request_field_site), state.site)
                SummaryRow(stringResource(R.string.request_review_needed_by), state.neededBy)
                if (state.note.isNotBlank()) {
                    SummaryRow(stringResource(R.string.request_field_note), state.note)
                }
            }
        }
        Text(
            stringResource(R.string.request_review_reviewer, reviewer),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    MaiButton(
        text = stringResource(R.string.request_send),
        onClick = { onAction(RequestCashAction.Send) },
        isLoading = state.submitting,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun SentStep(
    state: RequestCashState,
    onAction: (RequestCashAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaiTheme.extendedColors.successContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaiTheme.extendedColors.onSuccessContainer,
                modifier = Modifier.size(48.dp),
            )
        }
        Spacer(Modifier.size(MaiTheme.spacing.lg))
        Text(
            stringResource(R.string.request_sent_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        state.sentRequest?.let { request ->
            Spacer(Modifier.size(MaiTheme.spacing.sm))
            Text(
                request.voucherCode,
                style = MaiTheme.customTypography.codeMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { contentDescription = "Voucher ${request.voucherCode}" },
            )
        }
        Spacer(Modifier.size(MaiTheme.spacing.sm))
        Text(
            stringResource(R.string.request_sent_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.request_done),
            onClick = { onAction(RequestCashAction.Done) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaiTheme.spacing.xxl),
        )
    }
}

@Composable
private fun PickerField(
    value: String,
    label: String,
    hint: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Box(Modifier.clickable { expanded = true }) {
            MaiTextField(
                value = value,
                onValueChange = {},
                label = label,
                supportingText = hint,
                readOnly = true,
                placeholder = label,
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun NeededByField(value: String, onSelect: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    Column {
        Box(Modifier.clickable { showPicker = true }) {
            MaiTextField(
                value = value,
                onValueChange = {},
                label = stringResource(R.string.request_field_needed_by),
                supportingText = stringResource(R.string.request_field_needed_by_hint),
                readOnly = true,
                placeholder = stringResource(R.string.request_field_needed_by),
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
        if (showPicker) {
            DatePickerDialog(
                onDismissRequest = { showPicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onSelect(
                                    Instant.ofEpochMilli(millis)
                                        .atOffset(ZoneOffset.UTC)
                                        .format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH)),
                                )
                            }
                            showPicker = false
                        },
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showPicker = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
