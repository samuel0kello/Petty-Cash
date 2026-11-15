package com.example.pettycash.features.settings.impl.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.pettycash.features.settings.impl.R
import com.example.pettycash.features.settings.impl.data.SettingsProfile
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiSegmentedButton
import com.example.pettycash.mai.components.MaiSwitchRow
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun CompanyFormContent(profile: SettingsProfile, onChange: (SettingsProfile) -> Unit) {
    SettingsField(stringResource(R.string.settings_company_name), profile.companyName) {
        onChange(profile.copy(companyName = it))
    }
    SettingsField(stringResource(R.string.settings_industry), profile.industry ?: "") {
        onChange(profile.copy(industry = it.ifBlank { null }))
    }
    SettingsField(stringResource(R.string.settings_kra_pin), profile.kraPin) {
        onChange(profile.copy(kraPin = it))
    }
    SettingsField(stringResource(R.string.settings_business_reg), profile.businessReg) {
        onChange(profile.copy(businessReg = it))
    }
}

@Composable
internal fun LimitsFormContent(profile: SettingsProfile, onChange: (SettingsProfile) -> Unit) {
    SettingsField(stringResource(R.string.settings_single_spend_limit), profile.floatSingleSpendLimit) {
        onChange(profile.copy(floatSingleSpendLimit = it.filter(Char::isDigit)))
    }
    SettingsField(stringResource(R.string.settings_receipt_deadline), profile.floatReceiptDeadline) {
        onChange(profile.copy(floatReceiptDeadline = it))
    }
    SettingsField(stringResource(R.string.settings_no_receipt_limit), profile.floatNoReceiptLimit) {
        onChange(profile.copy(floatNoReceiptLimit = it.filter(Char::isDigit)))
    }
}

@Composable
internal fun ProjectsFormContent(profile: SettingsProfile, onChange: (SettingsProfile) -> Unit) {
    ChipEditor(
        label = stringResource(R.string.settings_projects_label),
        values = profile.projects,
        onChange = { onChange(profile.copy(projects = it)) },
    )
    ChipEditor(
        label = stringResource(R.string.settings_sites),
        values = profile.sites,
        onChange = { onChange(profile.copy(sites = it)) },
    )
}

@Composable
internal fun VouchersFormContent(profile: SettingsProfile, onChange: (SettingsProfile) -> Unit) {
    MaiSwitchRow(
        title = stringResource(R.string.settings_start_fresh),
        checked = profile.voucherStartFresh,
        onCheckedChange = { onChange(profile.copy(voucherStartFresh = it)) },
        modifier = Modifier.padding(horizontal = MaiTheme.spacing.sm),
    )
    SettingsField(stringResource(R.string.settings_last_voucher), profile.lastVoucherNumber) {
        onChange(profile.copy(lastVoucherNumber = it))
    }
}

@Composable
internal fun MpesaFormContent(profile: SettingsProfile, onChange: (SettingsProfile) -> Unit) {
    Text(
        stringResource(R.string.settings_mpesa_type),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
    MaiSegmentedButton(
        options = listOf("Paybill", "Till"),
        selected = profile.mpesaType,
        onSelect = { onChange(profile.copy(mpesaType = it)) },
        label = {
            stringResource(if (it == "Till") R.string.settings_till else R.string.settings_paybill)
        },
    )
    SettingsField(
        stringResource(
            R.string.settings_mpesa_number,
            if (profile.mpesaType == "Till") stringResource(R.string.settings_till) else stringResource(R.string.settings_paybill),
        ),
        profile.mpesaNumber,
    ) { onChange(profile.copy(mpesaNumber = it.filter(Char::isDigit))) }
    SettingsField(stringResource(R.string.settings_account_name), profile.mpesaAccountName) {
        onChange(profile.copy(mpesaAccountName = it))
    }
    if (profile.mpesaConnected) {
        Text(
            stringResource(R.string.settings_connected),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SettingsField(label: String, value: String, onChange: (String) -> Unit) {
    MaiTextField(value = value, onValueChange = onChange, label = label)
}

@Composable
private fun ChipEditor(label: String, values: List<String>, onChange: (List<String>) -> Unit) {
    var draft by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
            MaiTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = label,
                modifier = Modifier.weight(1f),
            )
            MaiButton(
                text = stringResource(R.string.settings_add),
                onClick = {
                    val v = draft.trim()
                    if (v.isNotEmpty() && v !in values) onChange(values + v)
                    draft = ""
                },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        values.forEach { value ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row {
                    TextButton(onClick = { editing = value }) {
                        Text(stringResource(R.string.settings_edit))
                    }
                    TextButton(onClick = { onChange(values - value) }) {
                        Text("✕")
                    }
                }
            }
        }
    }
    editing?.let { current ->
        var text by remember { mutableStateOf(current) }
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text(label) },
            text = {
                MaiTextField(value = text, onValueChange = { text = it })
            },
            confirmButton = {
                TextButton(onClick = {
                    onChange(values - current + text.trim())
                    editing = null
                }) { Text(stringResource(R.string.settings_save)) }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) { Text(stringResource(R.string.settings_cancel)) }
            },
        )
    }
}
