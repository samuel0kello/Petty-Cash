package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.StepLabel
import com.example.pettycash.mai.components.MaiSwitchRow
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun InviteTeamStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        StepLabel(step = state.stepNumber)
        Text(
            stringResource(R.string.company_invite_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        MaiTextField(
            value = state.invitePhone,
            onValueChange = { onAction(CompanyOnboardingAction.OnInvitePhoneChange(it)) },
            label = stringResource(R.string.company_invite_phone),
            supportingText = stringResource(R.string.company_invite_phone_hint),
            prefix = "+254",
            keyboardType = KeyboardType.Phone,
        )
        Text(
            stringResource(R.string.company_invite_role),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(Modifier.fillMaxWidth()) {
            MaiSwitchRow(
                title = stringResource(R.string.company_invite_approver),
                supportingText = stringResource(R.string.company_invite_approver_hint),
                checked = state.inviteIsApprover,
                onCheckedChange = { onAction(CompanyOnboardingAction.OnInviteApproverToggle(it)) },
            )
            MaiSwitchRow(
                title = stringResource(R.string.company_invite_custodian),
                supportingText = stringResource(R.string.company_invite_custodian_hint),
                checked = state.inviteIsCustodian,
                onCheckedChange = { onAction(CompanyOnboardingAction.OnInviteCustodianToggle(it)) },
            )
        }
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
}
