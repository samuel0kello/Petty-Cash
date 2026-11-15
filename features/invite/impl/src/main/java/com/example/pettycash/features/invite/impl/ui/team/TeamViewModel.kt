package com.example.pettycash.features.invite.impl.ui.team

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class TeamViewModel(
    private val inviteRepository: InviteRepository,
) : MviViewModel<TeamAction, TeamEvent, TeamState>(TeamState()) {

    override fun onStateSubscription() {
        viewModelScope.launch {
            inviteRepository.team.collectLatest { snapshot ->
                updateState {
                    it.copy(
                        companyName = snapshot.companyName,
                        members = snapshot.members,
                        invites = snapshot.invites,
                        loaded = true,
                    )
                }
            }
        }
    }

    override fun onAction(action: TeamAction) {
        when (action) {
            TeamAction.OnBack -> sendEvent(TeamEvent.NavigateBack)
            TeamAction.OnInvitePerson -> sendEvent(TeamEvent.NavigateToInviteForm)
            is TeamAction.OnInviteClick -> sendEvent(TeamEvent.NavigateToInvitePreview(action.inviteId))
        }
    }
}
