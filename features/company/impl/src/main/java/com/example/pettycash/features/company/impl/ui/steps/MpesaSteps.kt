package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.MpesaType
import com.example.pettycash.features.company.impl.ui.StepLabel
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiSegmentedButton
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun MpesaStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        StepLabel(step = state.stepNumber)
        MaiSegmentedButton(
            options = MpesaType.entries,
            selected = state.mpesaType,
            onSelect = { onAction(CompanyOnboardingAction.OnMpesaTypeSelect(it)) },
            label = { type ->
                stringResource(if (type == MpesaType.Paybill) R.string.company_mpesa_paybill else R.string.company_mpesa_till)
            },
        )
        MaiTextField(
            value = state.mpesaNumber,
            onValueChange = { onAction(CompanyOnboardingAction.OnMpesaNumberChange(it)) },
            label = stringResource(
                if (state.mpesaType == MpesaType.Paybill) R.string.company_mpesa_number else R.string.company_mpesa_till_number,
            ),
            supportingText = stringResource(R.string.company_mpesa_number_hint),
            keyboardType = KeyboardType.Number,
        )
        MaiTextField(
            value = state.mpesaAccountName,
            onValueChange = { onAction(CompanyOnboardingAction.OnMpesaAccountChange(it)) },
            label = stringResource(R.string.company_mpesa_account),
            supportingText = stringResource(R.string.company_mpesa_account_hint),
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
}

@Composable
internal fun MpesaConnectedStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaiTheme.spacing.xxl)
            .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
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
        Text(
            stringResource(
                R.string.company_mpesa_connected_title,
                stringResource(if (state.mpesaType == MpesaType.Paybill) R.string.company_mpesa_paybill else R.string.company_mpesa_till),
                state.mpesaNumber,
            ),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.company_mpesa_connected_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        MaiButton(
            text = stringResource(R.string.company_continue),
            onClick = { onAction(CompanyOnboardingAction.OnNext) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
