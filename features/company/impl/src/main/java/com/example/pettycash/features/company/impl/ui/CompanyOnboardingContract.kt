package com.example.pettycash.features.company.impl.ui

enum class OnboardingStep {
    CompanyDetails,
    Mpesa,
    MpesaConnected,
    FirstFloat,
    ApprovalRule,
    ProjectsSites,
    InviteTeam,
}

enum class MpesaType { Paybill, Till }

enum class ApproverRule { Admins, SelectedApprovers }

enum class VoucherMode { StartFresh, ContinuePaper }

internal data class CompanyOnboardingState(
    val step: OnboardingStep = OnboardingStep.CompanyDetails,
    val companyName: String = "",
    val industry: String? = null,
    val teamSize: String? = null,
    val kraPin: String = "",
    val businessReg: String = "",
    val mpesaType: MpesaType = MpesaType.Paybill,
    val mpesaNumber: String = "",
    val mpesaAccountName: String = "",
    val mpesaConnected: Boolean = false,
    val floatName: String = "",
    val floatStartingAmount: String = "",
    val floatCustodian: String = "",
    val floatSingleSpendLimit: String = "",
    val floatReceiptDeadline: String = "24 hours",
    val floatNoReceiptLimit: String = "",
    val approverRule: ApproverRule = ApproverRule.Admins,
    val categories: Set<String> = setOf("Transport"),
    val projects: List<String> = emptyList(),
    val sites: List<String> = emptyList(),
    val voucherMode: VoucherMode = VoucherMode.StartFresh,
    val lastVoucherNumber: String = "",
    val invitePhone: String = "",
    val inviteIsApprover: Boolean = false,
    val inviteIsCustodian: Boolean = false,
    val isSaving: Boolean = false,
) {
    val stepNumber: Int
        get() = when (step) {
            OnboardingStep.CompanyDetails -> 1
            OnboardingStep.Mpesa, OnboardingStep.MpesaConnected -> 2
            OnboardingStep.FirstFloat -> 3
            OnboardingStep.ApprovalRule -> 4
            OnboardingStep.ProjectsSites -> 4
            OnboardingStep.InviteTeam -> 5
        }

    val canContinue: Boolean
        get() = when (step) {
            OnboardingStep.CompanyDetails -> companyName.isNotBlank()
            OnboardingStep.FirstFloat ->
                floatName.isNotBlank() && floatStartingAmount.filter(Char::isDigit).toLongOrNull()?.let { it > 0 } == true
            OnboardingStep.Mpesa -> mpesaNumber.isNotBlank() && mpesaAccountName.isNotBlank()
            else -> true
        }
}

internal sealed interface CompanyOnboardingAction {
    data class OnCompanyNameChange(val value: String) : CompanyOnboardingAction
    data class OnIndustrySelect(val value: String) : CompanyOnboardingAction
    data class OnTeamSizeSelect(val value: String) : CompanyOnboardingAction
    data class OnKraPinChange(val value: String) : CompanyOnboardingAction
    data class OnBusinessRegChange(val value: String) : CompanyOnboardingAction
    data class OnMpesaTypeSelect(val value: MpesaType) : CompanyOnboardingAction
    data class OnMpesaNumberChange(val value: String) : CompanyOnboardingAction
    data class OnMpesaAccountChange(val value: String) : CompanyOnboardingAction
    data object OnConnectMpesa : CompanyOnboardingAction
    data object OnMpesaLater : CompanyOnboardingAction
    data class OnFloatNameChange(val value: String) : CompanyOnboardingAction
    data class OnFloatAmountChange(val value: String) : CompanyOnboardingAction
    data class OnFloatCustodianChange(val value: String) : CompanyOnboardingAction
    data class OnFloatSpendLimitChange(val value: String) : CompanyOnboardingAction
    data class OnFloatReceiptDeadlineChange(val value: String) : CompanyOnboardingAction
    data class OnFloatNoReceiptLimitChange(val value: String) : CompanyOnboardingAction
    data class OnApproverRuleSelect(val value: ApproverRule) : CompanyOnboardingAction
    data class OnCategoryToggle(val value: String) : CompanyOnboardingAction
    data class OnProjectAdd(val value: String) : CompanyOnboardingAction
    data class OnProjectRemove(val value: String) : CompanyOnboardingAction
    data class OnSiteAdd(val value: String) : CompanyOnboardingAction
    data class OnSiteRemove(val value: String) : CompanyOnboardingAction
    data class OnVoucherModeSelect(val value: VoucherMode) : CompanyOnboardingAction
    data class OnLastVoucherChange(val value: String) : CompanyOnboardingAction
    data class OnInvitePhoneChange(val value: String) : CompanyOnboardingAction
    data class OnInviteApproverToggle(val value: Boolean) : CompanyOnboardingAction
    data class OnInviteCustodianToggle(val value: Boolean) : CompanyOnboardingAction
    data object OnSendInvites : CompanyOnboardingAction
    data object OnSkipInvites : CompanyOnboardingAction
    data object OnNext : CompanyOnboardingAction
    data object OnBack : CompanyOnboardingAction
}

internal sealed interface CompanyOnboardingEvent {
    data object NavigateHome : CompanyOnboardingEvent
}
