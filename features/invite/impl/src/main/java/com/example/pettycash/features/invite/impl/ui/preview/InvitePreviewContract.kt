package com.example.pettycash.features.invite.impl.ui.preview

import com.example.pettycash.features.invite.impl.data.InvitePreviewStatus
import com.example.pettycash.features.invite.impl.data.PendingInvite

data class InvitePreviewState(
    val invite: PendingInvite? = null,
    val companyName: String = "",
    val status: InvitePreviewStatus = InvitePreviewStatus.Valid,
    val loaded: Boolean = false,
)

sealed interface InvitePreviewAction {
    data object OnAccept : InvitePreviewAction
    data object OnDecline : InvitePreviewAction
    data object OnErrorBack : InvitePreviewAction
}

sealed interface InvitePreviewEvent {
    data object NavigateToJoinDetails : InvitePreviewEvent
    data object NavigateBack : InvitePreviewEvent
}
