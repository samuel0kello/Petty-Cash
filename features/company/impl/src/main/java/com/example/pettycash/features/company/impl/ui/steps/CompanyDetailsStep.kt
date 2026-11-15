package com.example.pettycash.features.company.impl.ui.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.pettycash.features.company.impl.ui.StepLabel
import com.example.pettycash.mai.components.MaiChip
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.theme.MaiTheme

private val INDUSTRIES = listOf(
    R.string.company_industry_energy,
    R.string.company_industry_construction,
    R.string.company_industry_retail,
    R.string.company_industry_agriculture,
    R.string.company_industry_health,
    R.string.company_industry_education,
    R.string.company_industry_other,
)

private val TEAM_SIZES = listOf(
    R.string.company_team_1_5,
    R.string.company_team_6_20,
    R.string.company_team_21_50,
    R.string.company_team_50_plus,
)

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
internal fun CompanyDetailsStep(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg)) {
        StepLabel(step = state.stepNumber)
        MaiTextField(
            value = state.companyName,
            onValueChange = { onAction(CompanyOnboardingAction.OnCompanyNameChange(it)) },
            label = stringResource(R.string.company_details_company_name),
            supportingText = stringResource(R.string.company_details_company_name_hint),
        )
        IndustryPicker(state = state, onAction = onAction)
        Text(
            stringResource(R.string.company_details_team_size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
            TEAM_SIZES.forEach { res ->
                val label = stringResource(res)
                MaiChip(
                    label = label,
                    selected = state.teamSize == label,
                    onClick = { onAction(CompanyOnboardingAction.OnTeamSizeSelect(label)) },
                )
            }
        }
        MaiTextField(
            value = state.kraPin,
            onValueChange = { onAction(CompanyOnboardingAction.OnKraPinChange(it)) },
            label = stringResource(R.string.company_details_kra_pin),
            supportingText = stringResource(R.string.company_details_kra_pin_hint),
        )
        MaiTextField(
            value = state.businessReg,
            onValueChange = { onAction(CompanyOnboardingAction.OnBusinessRegChange(it)) },
            label = stringResource(R.string.company_details_reg),
            supportingText = stringResource(R.string.company_details_reg_hint),
        )
        Spacer(Modifier.height(MaiTheme.spacing.sm))
    }
}

@Composable
private fun IndustryPicker(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        androidx.compose.foundation.layout.Box(
            Modifier.clickable { expanded = true },
        ) {
            MaiTextField(
                value = state.industry ?: "",
                onValueChange = {},
                label = stringResource(R.string.company_details_industry),
                supportingText = stringResource(R.string.company_details_industry_hint),
                readOnly = true,
                placeholder = stringResource(R.string.company_details_industry),
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
            INDUSTRIES.forEach { res ->
                val label = stringResource(res)
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onAction(CompanyOnboardingAction.OnIndustrySelect(label))
                        expanded = false
                    },
                )
            }
        }
    }
}
