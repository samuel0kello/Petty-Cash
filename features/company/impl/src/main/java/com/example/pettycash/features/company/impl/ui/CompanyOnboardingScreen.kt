package com.example.pettycash.features.company.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.company.impl.R
import com.example.pettycash.features.company.impl.ui.steps.ApprovalRuleStep
import com.example.pettycash.features.company.impl.ui.steps.CompanyDetailsStep
import com.example.pettycash.features.company.impl.ui.steps.FirstFloatStep
import com.example.pettycash.features.company.impl.ui.steps.InviteTeamStep
import com.example.pettycash.features.company.impl.ui.steps.MpesaConnectedStep
import com.example.pettycash.features.company.impl.ui.steps.MpesaStep
import com.example.pettycash.features.company.impl.ui.steps.ProjectsSitesStep
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CompanyOnboardingRoute(
    onNavigateHome: () -> Unit,
    viewModel: CompanyOnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = CompanyOnboardingState())

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            CompanyOnboardingEvent.NavigateHome -> onNavigateHome()
        }
    }

    CompanyOnboardingScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun CompanyOnboardingScreen(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        if (state.step == OnboardingStep.MpesaConnected) {
            MpesaConnectedStep(state = state, onAction = onAction)
            return@Surface
        }
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = topBarTitle(state.step),
                onNavigationClick = { onAction(CompanyOnboardingAction.OnBack) },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg),
            ) {
                StepContent(state = state, onAction = onAction)
            }
            BottomBar(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun StepContent(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    when (state.step) {
        OnboardingStep.CompanyDetails -> CompanyDetailsStep(state, onAction)
        OnboardingStep.Mpesa -> MpesaStep(state, onAction)
        OnboardingStep.MpesaConnected -> Unit
        OnboardingStep.FirstFloat -> FirstFloatStep(state, onAction)
        OnboardingStep.ApprovalRule -> ApprovalRuleStep(state, onAction)
        OnboardingStep.ProjectsSites -> ProjectsSitesStep(state, onAction)
        OnboardingStep.InviteTeam -> InviteTeamStep(state, onAction)
    }
}

@Composable
private fun BottomBar(
    state: CompanyOnboardingState,
    onAction: (CompanyOnboardingAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl)
            .padding(top = MaiTheme.spacing.md, bottom = MaiTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        when (state.step) {
            OnboardingStep.Mpesa -> {
                MaiButton(
                    text = stringResource(R.string.company_mpesa_connect),
                    onClick = { onAction(CompanyOnboardingAction.OnConnectMpesa) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canContinue,
                )
                MaiButton(
                    text = stringResource(R.string.company_mpesa_later),
                    onClick = { onAction(CompanyOnboardingAction.OnMpesaLater) },
                    type = MaiButtonType.Text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OnboardingStep.InviteTeam -> {
                MaiButton(
                    text = stringResource(R.string.company_invite_contacts),
                    onClick = {},
                    type = MaiButtonType.Tonal,
                    modifier = Modifier.fillMaxWidth(),
                )
                MaiButton(
                    text = stringResource(R.string.company_invite_link),
                    onClick = {},
                    type = MaiButtonType.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(MaiTheme.spacing.xs))
                MaiButton(
                    text = stringResource(R.string.company_invite_send),
                    onClick = { onAction(CompanyOnboardingAction.OnSendInvites) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.invitePhone.length >= 9,
                    isLoading = state.isSaving,
                )
                MaiButton(
                    text = stringResource(R.string.company_invite_skip),
                    onClick = { onAction(CompanyOnboardingAction.OnSkipInvites) },
                    type = MaiButtonType.Text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OnboardingStep.MpesaConnected -> Unit
            else -> {
                MaiButton(
                    text = stringResource(R.string.company_next),
                    onClick = { onAction(CompanyOnboardingAction.OnNext) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canContinue,
                )
            }
        }
    }
}

@Composable
private fun topBarTitle(step: OnboardingStep): String = when (step) {
    OnboardingStep.CompanyDetails -> stringResource(R.string.company_setup_title)
    OnboardingStep.Mpesa, OnboardingStep.MpesaConnected -> stringResource(R.string.company_mpesa_title)
    OnboardingStep.FirstFloat -> stringResource(R.string.company_float_title)
    OnboardingStep.ApprovalRule -> stringResource(R.string.company_approval_title)
    OnboardingStep.ProjectsSites -> stringResource(R.string.company_projects_title)
    OnboardingStep.InviteTeam -> stringResource(R.string.company_invite_title)
}

@Composable
internal fun StepLabel(step: Int, total: Int = 5) {
    Text(
        stringResource(R.string.company_step, step, total),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
