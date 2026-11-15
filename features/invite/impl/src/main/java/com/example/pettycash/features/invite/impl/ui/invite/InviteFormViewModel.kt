package com.example.pettycash.features.invite.impl.ui.invite

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.launch

internal class InviteFormViewModel(
    private val inviteRepository: InviteRepository,
) : MviViewModel<InviteFormAction, InviteFormEvent, InviteFormState>(InviteFormState()) {

    override fun onAction(action: InviteFormAction) {
        when (action) {
            InviteFormAction.OnBack -> sendEvent(InviteFormEvent.NavigateBack)
            is InviteFormAction.OnNameChange -> updateState { it.copy(name = action.value) }
            is InviteFormAction.OnPhoneChange -> updateState { it.copy(phone = action.value) }
            is InviteFormAction.OnApproverChange -> updateState { it.copy(approver = action.value) }
            is InviteFormAction.OnCustodianChange -> updateState { it.copy(custodian = action.value) }
            InviteFormAction.OnCopyLink -> Unit
            InviteFormAction.OnSend -> send()
        }
    }

    private fun send() {
        val state = currentState
        if (state.isSending || state.name.isBlank() || state.phone.isBlank()) return
        updateState { it.copy(isSending = true) }
        viewModelScope.launch {
            inviteRepository.sendInvite(state.name, state.phone, state.approver, state.custodian)
            sendEvent(InviteFormEvent.NavigateToSent)
        }
    }
}
