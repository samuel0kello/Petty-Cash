package com.example.pettycash.features.refunds.impl.ui.claim

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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.example.pettycash.features.refunds.impl.R
import com.example.pettycash.features.refunds.impl.data.RefundContext
import com.example.pettycash.mai.components.MaiAmountInput
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiListItem
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClaimRefundRoute(
    onClaimRefundEvent: (ClaimRefundEvent) -> Unit,
    viewModel: ClaimRefundViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ClaimRefundState())
    CollectOneTimeEvent(viewModel.event) { onClaimRefundEvent(it) }
    ClaimRefundScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ClaimRefundScreen(
    state: ClaimRefundState,
    onAction: (ClaimRefundAction) -> Unit,
) {
    val showAppBar = state.step != RefundStep.Capture && state.step != RefundStep.Preview
    Column(modifier = Modifier.fillMaxSize()) {
        if (showAppBar) {
            MaiTopAppBar(
                title = stringResource(
                    when (state.step) {
                        RefundStep.Amount -> R.string.refund_title
                        RefundStep.Details -> R.string.refund_details_title
                        RefundStep.Proof -> R.string.refund_proof_title
                        RefundStep.RefundTo -> R.string.refund_refund_to_title
                        RefundStep.Review -> R.string.refund_review_title
                        RefundStep.Capture, RefundStep.Preview -> R.string.refund_capture_title
                    },
                ),
                onNavigationClick = { onAction(ClaimRefundAction.Back) },
            )
        }
        when (state.step) {
            RefundStep.Amount -> AmountStep(state, onAction)
            RefundStep.Details -> DetailsStep(state, onAction)
            RefundStep.Proof -> ProofStep(state, onAction)
            RefundStep.RefundTo -> RefundToStep(state, onAction)
            RefundStep.Review -> ReviewStep(state, onAction)
            RefundStep.Capture -> CaptureStep(onShutter = { onAction(ClaimRefundAction.Shutter) })
            RefundStep.Preview -> PreviewStep(onUse = { onAction(ClaimRefundAction.UsePhoto) }, onRetake = { onAction(ClaimRefundAction.Retake) })
        }
    }
}

@Composable
private fun ColumnScope.AmountStep(state: ClaimRefundState, onAction: (ClaimRefundAction) -> Unit) {
    Column(modifier = Modifier.weight(1f)) {
        MaiAmountInput(
            amountDigits = state.amountDigits,
            onKey = { onAction(ClaimRefundAction.AmountKey(it)) },
            onBackspace = { onAction(ClaimRefundAction.AmountBackspace) },
            label = stringResource(R.string.refund_amount_label),
            supportingText = stringResource(R.string.refund_amount_supporting),
            modifier = Modifier.weight(1f),
        )
    }
    MaiButton(
        text = stringResource(R.string.refund_next),
        onClick = { onAction(ClaimRefundAction.Next) },
        enabled = amount(state) > 0,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.DetailsStep(state: ClaimRefundState, onAction: (ClaimRefundAction) -> Unit) {
    val context = state.context
    val isTransport = state.category == "Transport"
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        MaiTextField(
            value = state.purpose,
            onValueChange = { onAction(ClaimRefundAction.PurposeChanged(it)) },
            label = stringResource(R.string.refund_field_purpose),
            supportingText = stringResource(R.string.refund_field_purpose_hint),
        )
        PickerField(
            value = state.category,
            label = stringResource(R.string.refund_field_category),
            hint = stringResource(R.string.refund_field_category_hint),
            options = context?.categories ?: emptyList(),
            onSelect = { onAction(ClaimRefundAction.CategoryChanged(it)) },
        )
        MaiTextField(
            value = state.date,
            onValueChange = { onAction(ClaimRefundAction.DateChanged(it)) },
            label = stringResource(R.string.refund_field_date),
            supportingText = stringResource(R.string.refund_field_date_hint),
        )
        if (isTransport) {
            MaiTextField(
                value = state.from,
                onValueChange = { onAction(ClaimRefundAction.FromChanged(it)) },
                label = stringResource(R.string.refund_field_from),
                supportingText = stringResource(R.string.refund_field_from_hint),
            )
            MaiTextField(
                value = state.to,
                onValueChange = { onAction(ClaimRefundAction.ToChanged(it)) },
                label = stringResource(R.string.refund_field_to),
                supportingText = stringResource(R.string.refund_field_to_hint),
            )
        }
        MaiTextField(
            value = state.note,
            onValueChange = { onAction(ClaimRefundAction.NoteChanged(it)) },
            label = stringResource(R.string.refund_field_note),
            supportingText = stringResource(R.string.refund_field_note_hint),
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
    MaiButton(
        text = stringResource(R.string.refund_next),
        onClick = { onAction(ClaimRefundAction.Next) },
        enabled = state.purpose.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ColumnScope.ProofStep(state: ClaimRefundState, onAction: (ClaimRefundAction) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        Text(
            stringResource(R.string.refund_proof_headline),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        ProofCard(
            icon = Icons.Filled.PhotoCamera,
            title = stringResource(R.string.refund_proof_photo),
            help = stringResource(R.string.refund_proof_photo_help),
            selected = state.proof == ProofChoice.Photo,
            onClick = { onAction(ClaimRefundAction.TakePhoto) },
            modifier = Modifier.fillMaxWidth(),
        )
        ProofCard(
            icon = Icons.Filled.ReceiptLong,
            title = stringResource(R.string.refund_proof_no_receipt),
            help = stringResource(R.string.refund_proof_no_receipt_help),
            selected = state.proof == ProofChoice.NoReceipt,
            onClick = { onAction(ClaimRefundAction.NoReceiptProof) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.weight(1f))
    }
    MaiButton(
        text = stringResource(R.string.refund_next),
        onClick = { onAction(ClaimRefundAction.Next) },
        enabled = state.proof != ProofChoice.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

@Composable
private fun ProofCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    help: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(86.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaiTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(help, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ColumnScope.RefundToStep(state: ClaimRefundState, onAction: (ClaimRefundAction) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        Text(
            stringResource(R.string.refund_refund_to_headline),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (state.refundToEditing) {
            MaiTextField(
                value = state.refundTo,
                onValueChange = { onAction(ClaimRefundAction.RefundToChanged(it)) },
                label = stringResource(R.string.refund_refund_to_label),
            )
            MaiButton(
                text = stringResource(R.string.refund_done),
                onClick = { onAction(ClaimRefundAction.RefundToDone) },
                enabled = state.refundTo.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MaiListItem(
                    title = maskPhone(state.refundTo),
                    subtitle = stringResource(R.string.refund_refund_to_label),
                    leadingIcon = Icons.Filled.PhoneAndroid,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { onAction(ClaimRefundAction.ChangeRefundTo) }) {
                    Text(stringResource(R.string.refund_change))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
    MaiButton(
        text = stringResource(R.string.refund_next),
        onClick = { onAction(ClaimRefundAction.Next) },
        enabled = state.refundTo.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
    )
}

private fun maskPhone(phone: String): String {
    if (phone.length < 7) return phone
    return phone.take(4) + " ••• " + phone.takeLast(3)
}

@Composable
private fun ColumnScope.ReviewStep(state: ClaimRefundState, onAction: (ClaimRefundAction) -> Unit) {
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
                ReviewRow(stringResource(R.string.refund_row_amount), "KES ${formatAmount(amount(state).toString())}")
                ReviewRow(stringResource(R.string.refund_row_purpose), state.purpose)
                if (state.category.isNotBlank()) ReviewRow(stringResource(R.string.refund_row_category), state.category)
                if (state.from.isNotBlank() || state.to.isNotBlank()) {
                    ReviewRow(stringResource(R.string.refund_row_from_to), "${state.from} – ${state.to}")
                }
                if (state.date.isNotBlank()) ReviewRow(stringResource(R.string.refund_row_date), state.date)
                ReviewRow(
                    stringResource(R.string.refund_row_proof),
                    if (state.proof == ProofChoice.Photo) "Photo" else "No receipt",
                )
                ReviewRow(stringResource(R.string.refund_row_refund_to), maskPhone(state.refundTo))
            }
        }
        Spacer(Modifier.height(MaiTheme.spacing.lg))
    }
    MaiButton(
        text = stringResource(R.string.refund_submit),
        onClick = { onAction(ClaimRefundAction.Submit) },
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
private fun ColumnScope.CaptureStep(onShutter: () -> Unit) {
    val shutterLabel = stringResource(R.string.refund_shutter)
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
            stringResource(R.string.refund_hold_still),
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
    }
}

@Composable
private fun ColumnScope.PreviewStep(onUse: () -> Unit, onRetake: () -> Unit) {
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
                stringResource(R.string.refund_preview_readable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = MaiTheme.spacing.sm),
            )
        }
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.refund_use_photo),
            onClick = onUse,
            modifier = Modifier.fillMaxWidth(),
        )
        MaiButton(
            text = stringResource(R.string.refund_retake),
            onClick = onRetake,
            type = MaiButtonType.Text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaiTheme.spacing.sm, bottom = MaiTheme.spacing.xxl),
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
