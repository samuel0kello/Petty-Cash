package com.example.pettycash.features.invite.impl.ui.team

import com.example.pettycash.features.invite.impl.data.TeamMember
import com.example.pettycash.features.invite.impl.data.PendingInvite

data class TeamState(
    val companyName: String = "",
    val members: List<TeamMember> = emptyList(),
    val invites: List<PendingInvite> = emptyList(),
    val loaded: Boolean = false,
)

sealed interface TeamAction {
    data object OnBack : TeamAction
    data object OnInvitePerson : TeamAction
    data class OnInviteClick(val inviteId: String) : TeamAction
}

sealed interface TeamEvent {
    data object NavigateBack : TeamEvent
    data object NavigateToInviteForm : TeamEvent
    data class NavigateToInvitePreview(val inviteId: String) : TeamEvent
}
