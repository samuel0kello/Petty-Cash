package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.VoucherMode
import com.example.pettycash.mai.components.MaiChip
import com.example.pettycash.mai.components.MaiChipVariant
import com.example.pettycash.mai.components.MaiRadioRow
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProjectsSitesStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    var projectDialog by remember { mutableStateOf(false) }
    var siteDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        Text(
            stringResource(R.string.company_projects_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            state.projects.forEach { project ->
                MaiChip(
                    label = project,
                    trailingIcon = Icons.Filled.Close,
                    onClick = { onAction(CompanyOnboardingAction.OnProjectRemove(project)) },
                )
            }
            MaiChip(
                label = stringResource(R.string.company_projects_add),
                variant = MaiChipVariant.Assist,
                leadingIcon = Icons.Filled.Add,
                onClick = { projectDialog = true },
            )
        }
        Text(
            stringResource(R.string.company_sites_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            state.sites.forEach { site ->
                MaiChip(
                    label = site,
                    trailingIcon = Icons.Filled.Close,
                    onClick = { onAction(CompanyOnboardingAction.OnSiteRemove(site)) },
                )
            }
            MaiChip(
                label = stringResource(R.string.company_sites_add),
                variant = MaiChipVariant.Assist,
                leadingIcon = Icons.Filled.Add,
                onClick = { siteDialog = true },
            )
        }
        Text(
            stringResource(R.string.company_vouchers_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        MaiRadioRow(
            label = stringResource(R.string.company_vouchers_fresh),
            selected = state.voucherMode == VoucherMode.StartFresh,
            onClick = { onAction(CompanyOnboardingAction.OnVoucherModeSelect(VoucherMode.StartFresh)) },
        )
        MaiRadioRow(
            label = stringResource(R.string.company_vouchers_continue),
            selected = state.voucherMode == VoucherMode.ContinuePaper,
            onClick = { onAction(CompanyOnboardingAction.OnVoucherModeSelect(VoucherMode.ContinuePaper)) },
        )
        MaiTextField(
            value = state.lastVoucherNumber,
            onValueChange = { onAction(CompanyOnboardingAction.OnLastVoucherChange(it)) },
            label = stringResource(R.string.company_vouchers_last),
            supportingText = stringResource(R.string.company_vouchers_last_hint),
            enabled = state.voucherMode == VoucherMode.ContinuePaper,
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }

    if (projectDialog) {
        NameEntryDialog(
            title = stringResource(R.string.company_projects_dialog_title),
            hint = stringResource(R.string.company_projects_name_hint),
            onDismiss = { projectDialog = false },
            onConfirm = {
                onAction(CompanyOnboardingAction.OnProjectAdd(it))
                projectDialog = false
            },
        )
    }
    if (siteDialog) {
        NameEntryDialog(
            title = stringResource(R.string.company_sites_dialog_title),
            hint = stringResource(R.string.company_sites_name_hint),
            onDismiss = { siteDialog = false },
            onConfirm = {
                onAction(CompanyOnboardingAction.OnSiteAdd(it))
                siteDialog = false
            },
        )
    }
}

@Composable
private fun NameEntryDialog(
    title: String,
    hint: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            MaiTextField(
                value = value,
                onValueChange = { value = it },
                label = hint,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }, enabled = value.isNotBlank()) {
                Text(stringResource(R.string.company_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.company_cancel))
            }
        },
    )
}
