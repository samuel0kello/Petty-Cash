package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.ApproverRule
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.StepLabel
import com.example.pettycash.mai.components.MaiBanner
import com.example.pettycash.mai.components.MaiBannerTone
import com.example.pettycash.mai.components.MaiChip
import com.example.pettycash.mai.components.MaiRadioRow
import com.example.pettycash.mai.theme.MaiTheme

private val CATEGORIES = listOf(
    R.string.company_cat_transport,
    R.string.company_cat_office,
    R.string.company_cat_fuel,
    R.string.company_cat_meals,
    R.string.company_cat_repairs,
    R.string.company_cat_other,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ApprovalRuleStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        StepLabel(step = state.stepNumber)
        Text(
            stringResource(R.string.company_approval_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        MaiRadioRow(
            label = stringResource(R.string.company_approval_admins),
            selected = state.approverRule == ApproverRule.Admins,
            onClick = { onAction(CompanyOnboardingAction.OnApproverRuleSelect(ApproverRule.Admins)) },
        )
        MaiRadioRow(
            label = stringResource(R.string.company_approval_selected),
            selected = state.approverRule == ApproverRule.SelectedApprovers,
            onClick = { onAction(CompanyOnboardingAction.OnApproverRuleSelect(ApproverRule.SelectedApprovers)) },
        )
        MaiBanner(
            message = stringResource(R.string.company_approval_banner),
            tone = MaiBannerTone.Info,
            icon = Icons.Filled.Info,
        )
        Text(
            stringResource(R.string.company_approval_categories),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            CATEGORIES.forEach { res ->
                val label = stringResource(res)
                MaiChip(
                    label = label,
                    selected = label in state.categories,
                    onClick = { onAction(CompanyOnboardingAction.OnCategoryToggle(label)) },
                )
            }
        }
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
}
