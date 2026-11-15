package com.example.pettycash.features.invite.impl.ui.invite

import com.example.pettycash.features.invite.impl.data.PendingInvite

data class InviteFormState(
    val name: String = "",
    val phone: String = "",
    val approver: Boolean = false,
    val custodian: Boolean = false,
    val isSending: Boolean = false,
)

sealed interface InviteFormAction {
    data object OnBack : InviteFormAction
    data class OnNameChange(val value: String) : InviteFormAction
    data class OnPhoneChange(val value: String) : InviteFormAction
    data class OnApproverChange(val value: Boolean) : InviteFormAction
    data class OnCustodianChange(val value: Boolean) : InviteFormAction
    data object OnCopyLink : InviteFormAction
    data object OnSend : InviteFormAction
}

sealed interface InviteFormEvent {
    data object NavigateBack : InviteFormEvent
    data object NavigateToSent : InviteFormEvent
}
