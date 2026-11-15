package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.StepLabel
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun FirstFloatStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        StepLabel(step = state.stepNumber)
        MaiTextField(
            value = state.floatName,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatNameChange(it)) },
            label = stringResource(R.string.company_float_name),
            supportingText = stringResource(R.string.company_float_name_hint),
        )
        MaiTextField(
            value = state.floatStartingAmount,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatAmountChange(it)) },
            label = stringResource(R.string.company_float_amount),
            supportingText = stringResource(R.string.company_float_amount_hint),
            prefix = "KES",
            keyboardType = KeyboardType.Number,
        )
        MaiTextField(
            value = state.floatCustodian,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatCustodianChange(it)) },
            label = stringResource(R.string.company_float_custodian),
            supportingText = stringResource(R.string.company_float_custodian_hint),
            placeholder = stringResource(R.string.company_float_custodian_later),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
        MaiTextField(
            value = state.floatSingleSpendLimit,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatSpendLimitChange(it)) },
            label = stringResource(R.string.company_float_spend_limit),
            supportingText = stringResource(R.string.company_float_spend_limit_hint),
            prefix = "KES",
            keyboardType = KeyboardType.Number,
        )
        MaiTextField(
            value = state.floatReceiptDeadline,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatReceiptDeadlineChange(it)) },
            label = stringResource(R.string.company_float_receipt_deadline),
            supportingText = stringResource(R.string.company_float_receipt_deadline_hint),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
        MaiTextField(
            value = state.floatNoReceiptLimit,
            onValueChange = { onAction(CompanyOnboardingAction.OnFloatNoReceiptLimitChange(it)) },
            label = stringResource(R.string.company_float_no_receipt_limit),
            supportingText = stringResource(R.string.company_float_no_receipt_hint),
            prefix = "KES",
            keyboardType = KeyboardType.Number,
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
}
