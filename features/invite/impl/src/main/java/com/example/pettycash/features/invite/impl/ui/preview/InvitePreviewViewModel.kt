package com.example.pettycash.features.invite.impl.ui.preview

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.invite.impl.data.InvitePreviewStatus
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class InvitePreviewViewModel(
    private val inviteRepository: InviteRepository,
    private val inviteId: String,
) : MviViewModel<InvitePreviewAction, InvitePreviewEvent, InvitePreviewState>(InvitePreviewState()) {

    override fun onStateSubscription() {
        viewModelScope.launch {
            val invite = inviteRepository.inviteById(inviteId)
            val companyName = inviteRepository.draft.first().companyName
            val status = when (invite?.status) {
                "expired" -> InvitePreviewStatus.Expired
                "wrong_number" -> InvitePreviewStatus.WrongNumber
                else -> InvitePreviewStatus.Valid
            }
            updateState {
                it.copy(invite = invite, companyName = companyName, status = status, loaded = true)
            }
        }
    }

    override fun onAction(action: InvitePreviewAction) {
        when (action) {
            InvitePreviewAction.OnAccept -> {
                viewModelScope.launch {
                    inviteRepository.beginJoin(inviteId)
                    sendEvent(InvitePreviewEvent.NavigateToJoinDetails)
                }
            }
            InvitePreviewAction.OnDecline, InvitePreviewAction.OnErrorBack ->
                sendEvent(InvitePreviewEvent.NavigateBack)
        }
    }
}
