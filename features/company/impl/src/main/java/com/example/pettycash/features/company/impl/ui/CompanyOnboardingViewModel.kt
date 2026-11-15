package com.example.pettycash.features.company.impl.ui

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.company.impl.data.CompanyProfile
import com.example.pettycash.features.company.impl.data.CompanyRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.launch

internal class CompanyOnboardingViewModel(
    private val companyRepository: CompanyRepository,
) : MviViewModel<CompanyOnboardingAction, CompanyOnboardingEvent, CompanyOnboardingState>(
    CompanyOnboardingState(),
) {

    override fun onAction(action: CompanyOnboardingAction) {
        when (action) {
            is CompanyOnboardingAction.OnCompanyNameChange -> updateState { it.copy(companyName = action.value) }
            is CompanyOnboardingAction.OnIndustrySelect -> updateState { it.copy(industry = action.value) }
            is CompanyOnboardingAction.OnTeamSizeSelect -> updateState { it.copy(teamSize = action.value) }
            is CompanyOnboardingAction.OnKraPinChange -> updateState { it.copy(kraPin = action.value) }
            is CompanyOnboardingAction.OnBusinessRegChange -> updateState { it.copy(businessReg = action.value) }
            is CompanyOnboardingAction.OnMpesaTypeSelect -> updateState { it.copy(mpesaType = action.value) }
            is CompanyOnboardingAction.OnMpesaNumberChange ->
                updateState { it.copy(mpesaNumber = action.value.filter(Char::isDigit).take(10)) }
            is CompanyOnboardingAction.OnMpesaAccountChange -> updateState { it.copy(mpesaAccountName = action.value) }
            CompanyOnboardingAction.OnConnectMpesa -> {
                if (!currentState.canContinue) return
                updateState { it.copy(mpesaConnected = true, step = OnboardingStep.MpesaConnected) }
            }
            CompanyOnboardingAction.OnMpesaLater -> goTo(OnboardingStep.FirstFloat)
            is CompanyOnboardingAction.OnFloatNameChange -> updateState { it.copy(floatName = action.value) }
            is CompanyOnboardingAction.OnFloatAmountChange ->
                updateState { it.copy(floatStartingAmount = action.value.filter(Char::isDigit).take(12)) }
            is CompanyOnboardingAction.OnFloatCustodianChange -> updateState { it.copy(floatCustodian = action.value) }
            is CompanyOnboardingAction.OnFloatSpendLimitChange ->
                updateState { it.copy(floatSingleSpendLimit = action.value.filter(Char::isDigit).take(12)) }
            is CompanyOnboardingAction.OnFloatReceiptDeadlineChange ->
                updateState { it.copy(floatReceiptDeadline = action.value) }
            is CompanyOnboardingAction.OnFloatNoReceiptLimitChange ->
                updateState { it.copy(floatNoReceiptLimit = action.value.filter(Char::isDigit).take(12)) }
            is CompanyOnboardingAction.OnApproverRuleSelect -> updateState { it.copy(approverRule = action.value) }
            is CompanyOnboardingAction.OnCategoryToggle -> updateState {
                it.copy(categories = if (action.value in it.categories) it.categories - action.value else it.categories + action.value)
            }
            is CompanyOnboardingAction.OnProjectAdd -> updateState {
                if (action.value.isNotBlank() && action.value !in it.projects) it.copy(projects = it.projects + action.value.trim()) else it
            }
            is CompanyOnboardingAction.OnProjectRemove -> updateState { it.copy(projects = it.projects - action.value) }
            is CompanyOnboardingAction.OnSiteAdd -> updateState {
                if (action.value.isNotBlank() && action.value !in it.sites) it.copy(sites = it.sites + action.value.trim()) else it
            }
            is CompanyOnboardingAction.OnSiteRemove -> updateState { it.copy(sites = it.sites - action.value) }
            is CompanyOnboardingAction.OnVoucherModeSelect -> updateState { it.copy(voucherMode = action.value) }
            is CompanyOnboardingAction.OnLastVoucherChange ->
                updateState { it.copy(lastVoucherNumber = action.value.uppercase().filter { it.isLetterOrDigit() || it == '-' }.take(12)) }
            is CompanyOnboardingAction.OnInvitePhoneChange ->
                updateState { it.copy(invitePhone = action.value.filter(Char::isDigit).take(10)) }
            is CompanyOnboardingAction.OnInviteApproverToggle -> updateState { it.copy(inviteIsApprover = action.value) }
            is CompanyOnboardingAction.OnInviteCustodianToggle -> updateState { it.copy(inviteIsCustodian = action.value) }
            CompanyOnboardingAction.OnSendInvites -> finish()
            CompanyOnboardingAction.OnSkipInvites -> finish()
            CompanyOnboardingAction.OnNext -> advance()
            CompanyOnboardingAction.OnBack -> goBack()
        }
    }

    private fun advance() {
        if (!currentState.canContinue) return
        when (currentState.step) {
            OnboardingStep.CompanyDetails -> goTo(OnboardingStep.Mpesa)
            OnboardingStep.MpesaConnected -> goTo(OnboardingStep.FirstFloat)
            OnboardingStep.FirstFloat -> goTo(OnboardingStep.ApprovalRule)
            OnboardingStep.ApprovalRule -> goTo(OnboardingStep.ProjectsSites)
            OnboardingStep.ProjectsSites -> goTo(OnboardingStep.InviteTeam)
            else -> Unit
        }
    }

    private fun goBack() {
        when (currentState.step) {
            OnboardingStep.Mpesa -> goTo(OnboardingStep.CompanyDetails)
            OnboardingStep.MpesaConnected -> goTo(OnboardingStep.Mpesa)
            OnboardingStep.FirstFloat -> goTo(OnboardingStep.Mpesa)
            OnboardingStep.ApprovalRule -> goTo(OnboardingStep.FirstFloat)
            OnboardingStep.ProjectsSites -> goTo(OnboardingStep.ApprovalRule)
            OnboardingStep.InviteTeam -> goTo(OnboardingStep.ProjectsSites)
            OnboardingStep.CompanyDetails -> Unit
        }
    }

    private fun goTo(step: OnboardingStep) {
        updateState { it.copy(step = step) }
    }

    private fun finish() {
        if (currentState.isSaving) return
        updateState { it.copy(isSaving = true) }
        viewModelScope.launch {
            val s = currentState
            companyRepository.completeOnboarding(
                CompanyProfile(
                    companyName = s.companyName.trim(),
                    industry = s.industry,
                    teamSize = s.teamSize,
                    kraPin = s.kraPin.trim(),
                    businessReg = s.businessReg.trim(),
                    mpesaType = s.mpesaType.name,
                    mpesaNumber = s.mpesaNumber,
                    mpesaAccountName = s.mpesaAccountName.trim(),
                    mpesaConnected = s.mpesaConnected,
                    floatName = s.floatName.trim(),
                    floatStartingAmount = s.floatStartingAmount,
                    floatCustodian = s.floatCustodian.trim(),
                    floatSingleSpendLimit = s.floatSingleSpendLimit,
                    floatReceiptDeadline = s.floatReceiptDeadline,
                    floatNoReceiptLimit = s.floatNoReceiptLimit,
                    approverRule = s.approverRule.name,
                    categories = s.categories.toList(),
                    projects = s.projects,
                    sites = s.sites,
                    voucherStartFresh = s.voucherMode == VoucherMode.StartFresh,
                    lastVoucherNumber = s.lastVoucherNumber,
                ),
            )
            sendEvent(CompanyOnboardingEvent.NavigateHome)
        }
    }
}
