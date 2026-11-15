package com.example.pettycash.features.spends.impl.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.pettycash.features.spends.impl.R
import com.example.pettycash.features.spends.impl.data.SpendContext
import com.example.pettycash.features.spends.impl.data.SpendItem
import com.example.pettycash.mai.components.MaiAmountInput
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiSegmentedButton
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RecordSpendRoute(
    onRecordSpendEvent: (RecordSpendEvent) -> Unit,
    viewModel: RecordSpendViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = RecordSpendState())
    CollectOneTimeEvent(viewModel.event) { onRecordSpendEvent(it) }
    RecordSpendScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun RecordSpendScreen(
    state: RecordSpendState,
    onAction: (RecordSpendAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.step != SpendStep.Receipt || state.receiptStage != ReceiptStage.Capture) {
            MaiTopAppBar(
                title = stringResource(
                    when (state.step) {
                        SpendStep.Amount -> R.string.spend_title
                        SpendStep.Details -> R.string.spend_details_title
                        SpendStep.PaidWith -> R.string.spend_paid_with_title
                        SpendStep.Receipt -> R.string.spend_receipt_title
                        SpendStep.Review -> R.string.spend_review_title
                    },
                ),
                onNavigationClick = { onAction(if (state.step == SpendStep.Receipt) RecordSpendAction.BackToCapture else RecordSpendAction.Back) },
            )
        }
        when (state.step) {
            SpendStep.Amount -> AmountStep(state, onAction)
            SpendStep.Details -> DetailsStep(state, onAction)
            SpendStep.PaidWith -> PaidWithStep(state, onAction)
            SpendStep.Receipt -> ReceiptStep(state, onAction)
            SpendStep.Review -> ReviewStep(state, onAction)
        }
    }
}

@Composable
private fun ColumnScope.AmountStep(state: RecordSpendState, onAction: (RecordSpendAction) -> Unit) {
    val float = state.context?.floatKes ?: 0L
    val spend = amount(state)
    val tooLow = spend > float && spend > 0
    Column(modifier = Modifier.weight(1f)) {
        MaiAmountInput(
            amountDigits = state.amountDigits,
            onKey = { onAction(RecordSpendAction.AmountKey(it)) },
            onBackspace = { onAction(RecordSpendAction.AmountBackspace) },
            label = stringResource(R.string.spend_amount_label),
            supportingText = stringResource(
                if (tooLow) R.string.spend_amount_too_low else R.string.spend_amount_float_after,
                formatAmount((float - spend).coerceAtLeast(0).toString()),
            ),
            isWarning = tooLow,
            modifier = Modifier.weight(1f),
        )
    }
    MaiButton(
        text = stringResource(R.string.spend_next),
        onClick = { onAction(RecordSpendAction.Next) },
        enabled = spend > 0 && !tooLow,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.DetailsStep(state: RecordSpendState, onAction: (RecordSpendAction) -> Unit) {
    val context = state.context
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md)) {
            Text(
                stringResource(R.string.spend_line_items),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            state.items.forEachIndexed { index, item ->
                SpendItemRow(
                    item = item,
                    onDescription = { onAction(RecordSpendAction.ItemDescription(index, it)) },
                    onAmount = { onAction(RecordSpendAction.ItemAmount(index, it)) },
                    onRemove = { onAction(RecordSpendAction.RemoveItem(index)) },
                )
            }
            TextButton(onClick = { onAction(RecordSpendAction.AddItem) }) {
                Text(
                    stringResource(R.string.spend_add_item),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.spend_total),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "KES ${formatAmount(itemsTotal(state).toString())}",
                    style = MaiTheme.customTypography.amountMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        PickerField(
            value = state.category,
            label = stringResource(R.string.spend_field_category),
            hint = stringResource(R.string.spend_field_category_hint),
            options = context?.categories ?: emptyList(),
            onSelect = { onAction(RecordSpendAction.CategoryChanged(it)) },
        )
        if (!context?.projects.isNullOrEmpty()) {
            PickerField(
                value = state.project,
                label = stringResource(R.string.spend_field_project),
                hint = stringResource(R.string.spend_field_project_hint),
                options = context.projects,
                onSelect = { onAction(RecordSpendAction.ProjectChanged(it)) },
            )
        } else {
            MaiTextField(
                value = state.project,
                onValueChange = { onAction(RecordSpendAction.ProjectChanged(it)) },
                label = stringResource(R.string.spend_field_project),
                supportingText = stringResource(R.string.spend_field_project_hint),
            )
        }
        if (!context?.sites.isNullOrEmpty()) {
            PickerField(
                value = state.site,
                label = stringResource(R.string.spend_field_site),
                hint = stringResource(R.string.spend_field_site_hint),
                options = context.sites,
                onSelect = { onAction(RecordSpendAction.SiteChanged(it)) },
            )
        } else {
            MaiTextField(
                value = state.site,
                onValueChange = { onAction(RecordSpendAction.SiteChanged(it)) },
                label = stringResource(R.string.spend_field_site),
                supportingText = stringResource(R.string.spend_field_site_hint),
            )
        }
        MaiTextField(
            value = state.paidTo,
            onValueChange = { onAction(RecordSpendAction.PaidToChanged(it)) },
            label = stringResource(R.string.spend_field_paid_to),
            supportingText = stringResource(R.string.spend_field_paid_to_hint),
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
    MaiButton(
        text = stringResource(R.string.spend_next),
        onClick = { onAction(RecordSpendAction.Next) },
        enabled = itemsTotal(state) > 0,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun SpendItemRow(
    item: SpendItem,
    onDescription: (String) -> Unit,
    onAmount: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompactField(
            label = stringResource(R.string.spend_item_description),
            value = item.description,
            onValueChange = onDescription,
            modifier = Modifier.weight(1f),
        )
        CompactField(
            label = stringResource(R.string.spend_item_amount),
            value = if (item.amountKes > 0) item.amountKes.toString() else "",
            onValueChange = onAmount,
            modifier = Modifier.size(width = 112.dp, height = 56.dp),
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.spend_delete_item),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun CompactField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .padding(horizontal = MaiTheme.spacing.md),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColumnScope.PaidWithStep(state: RecordSpendState, onAction: (RecordSpendAction) -> Unit) {
    val context = state.context
    val spend = itemsTotal(state)
    val codeMatched = state.mpesaCode.length >= 10
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        MaiSegmentedButton(
            options = PayMethod.entries,
            selected = state.method,
            onSelect = { onAction(RecordSpendAction.MethodSelected(it)) },
            label = {
                stringResource(
                    when (it) {
                        PayMethod.Mpesa -> R.string.spend_method_mpesa
                        PayMethod.Cash -> R.string.spend_method_cash
                    },
                )
            },
        )
        if (state.method == PayMethod.Mpesa) {
            MpesaCodeField(
                code = state.mpesaCode,
                matched = codeMatched,
                onChange = { onAction(RecordSpendAction.MpesaCodeChanged(it)) },
            )
            if (codeMatched) {
                val payee = state.paidTo.ifBlank { "M-PESA" }
                val today = SimpleDateFormat("d MMM", Locale.US).format(Date())
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaiTheme.extendedColors.successContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = MaiTheme.spacing.md,
                            vertical = MaiTheme.spacing.sm,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaiTheme.extendedColors.onSuccessContainer,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            stringResource(
                                R.string.spend_mpesa_matched,
                                formatAmount(spend.toString()),
                                payee.uppercase(),
                                today,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaiTheme.extendedColors.onSuccessContainer,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
    MaiButton(
        text = stringResource(R.string.spend_next),
        onClick = { onAction(RecordSpendAction.Next) },
        enabled = state.method == PayMethod.Cash || state.mpesaCode.length >= 6,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun MpesaCodeField(code: String, matched: Boolean, onChange: (String) -> Unit) {
    val mpesaCodeLabel = stringResource(R.string.spend_mpesa_code)
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(horizontal = MaiTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.spend_mpesa_code),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                BasicTextField(
                    value = code,
                    onValueChange = onChange,
                    textStyle = MaiTheme.customTypography.codeMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = mpesaCodeLabel },
                )
            }
            if (matched) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ReceiptStep(state: RecordSpendState, onAction: (RecordSpendAction) -> Unit) {
    when (state.receiptStage) {
        ReceiptStage.Capture -> ReceiptCapture(onShutter = { onAction(RecordSpendAction.Shutter) }, onNoReceipt = { onAction(RecordSpendAction.NoReceipt) })
        ReceiptStage.Preview -> ReceiptPreview(onUse = { onAction(RecordSpendAction.UsePhoto) }, onRetake = { onAction(RecordSpendAction.Retake) })
        ReceiptStage.NoReceipt -> NoReceiptConfirm(onContinue = { onAction(RecordSpendAction.ContinueWithoutReceipt) }, onBack = { onAction(RecordSpendAction.BackToCapture) })
    }
}

@Composable
private fun ReceiptCapture(onShutter: () -> Unit, onNoReceipt: () -> Unit) {
    val shutterLabel = stringResource(R.string.spend_shutter)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(bottom = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .height(280.dp)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(64.dp),
            )
        }
        Text(
            stringResource(R.string.spend_hold_still),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.padding(top = MaiTheme.spacing.lg),
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(4.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                .padding(6.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .clickable(onClick = onShutter)
                .semantics { contentDescription = shutterLabel },
        )
        Spacer(Modifier.height(MaiTheme.spacing.lg))
        MaiButton(
            text = stringResource(R.string.spend_no_receipt),
            onClick = onNoReceipt,
            type = MaiButtonType.Tonal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl),
        )
    }
}

@Composable
private fun ReceiptPreview(onUse: () -> Unit, onRetake: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaiTheme.spacing.md),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                stringResource(R.string.spend_preview_readable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = MaiTheme.spacing.sm),
            )
        }
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.spend_use_photo),
            onClick = onUse,
            modifier = Modifier.fillMaxWidth(),
        )
        MaiButton(
            text = stringResource(R.string.spend_retake),
            onClick = onRetake,
            type = MaiButtonType.Text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaiTheme.spacing.sm),
        )
        Spacer(Modifier.height(MaiTheme.spacing.xxl))
    }
}

@Composable
private fun NoReceiptConfirm(onContinue: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.spend_no_receipt_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.spend_no_receipt_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MaiTheme.spacing.md),
        )
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.spend_continue_without),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )
        MaiButton(
            text = stringResource(R.string.spend_back),
            onClick = onBack,
            type = MaiButtonType.Text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaiTheme.spacing.sm, bottom = MaiTheme.spacing.xxl),
        )
    }
}

@Composable
private fun ColumnScope.ReviewStep(state: RecordSpendState, onAction: (RecordSpendAction) -> Unit) {
    val context = state.context
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    ) {
        MaiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            ) {
                ReviewRow(stringResource(R.string.spend_row_amount), "KES ${formatAmount(itemsTotal(state).toString())}")
                ReviewRow(
                    stringResource(R.string.spend_row_paid_with),
                    if (state.method == PayMethod.Mpesa) "M-Pesa ${state.mpesaCode}" else stringResource(R.string.spend_cash_value),
                )
                if (state.items.any { it.description.isNotBlank() }) {
                    ReviewRow(
                        "Items",
                        state.items.filter { it.description.isNotBlank() }
                            .joinToString("\n") { "${it.description} · KES ${formatAmount(it.amountKes.toString())}" },
                    )
                }
                if (state.category.isNotBlank()) ReviewRow(stringResource(R.string.spend_row_category), state.category)
                if (state.project.isNotBlank()) ReviewRow(stringResource(R.string.spend_row_project), state.project)
                if (state.site.isNotBlank()) ReviewRow(stringResource(R.string.spend_row_site), state.site)
                if (state.paidTo.isNotBlank()) ReviewRow(stringResource(R.string.spend_row_paid_to), state.paidTo)
                ReviewRow(
                    stringResource(R.string.spend_row_receipt),
                    stringResource(if (state.hasReceipt) R.string.spend_receipt_attached else R.string.spend_receipt_none),
                )
                if (context != null) {
                    ReviewRow("Float left", "KES ${formatAmount((context.floatKes - itemsTotal(state)).coerceAtLeast(0).toString())}")
                }
            }
        }
        Spacer(Modifier.height(MaiTheme.spacing.lg))
    }
    MaiButton(
        text = stringResource(R.string.spend_save),
        onClick = { onAction(RecordSpendAction.Save) },
        enabled = !state.saving,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
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
