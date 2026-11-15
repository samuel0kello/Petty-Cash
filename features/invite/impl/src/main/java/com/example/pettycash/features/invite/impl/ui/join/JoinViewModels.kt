package com.example.pettycash.features.invite.impl.ui.join

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.invite.impl.data.InviteDraft
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class JoinDetailsViewModel(
    private val inviteRepository: InviteRepository,
) : MviViewModel<JoinDetailsAction, JoinDetailsEvent, JoinDetailsState>(JoinDetailsState()) {

    override fun onStateSubscription() {
        viewModelScope.launch {
            inviteRepository.draft.collectLatest { draft ->
                updateState {
                    it.copy(
                        name = if (it.loaded) it.name else draft.name,
                        phone = if (it.loaded) it.phone else draft.phone,
                        companyName = draft.companyName,
                        loaded = true,
                    )
                }
            }
        }
    }

    override fun onAction(action: JoinDetailsAction) {
        when (action) {
            JoinDetailsAction.OnBack -> sendEvent(JoinDetailsEvent.NavigateBack)
            is JoinDetailsAction.OnNameChange -> updateState { it.copy(name = action.value) }
            is JoinDetailsAction.OnPhoneChange -> updateState { it.copy(phone = action.value) }
            JoinDetailsAction.OnContinue -> continueJoin()
        }
    }

    private fun continueJoin() {
        val state = currentState
        if (state.name.isBlank()) return
        viewModelScope.launch {
            inviteRepository.updateDraft(state.name, state.phone)
            sendEvent(JoinDetailsEvent.NavigateToData)
        }
    }
}

internal class JoinDataViewModel(
    private val inviteRepository: InviteRepository,
) : MviViewModel<JoinDataAction, JoinDataEvent, JoinDataState>(JoinDataState()) {

    override fun onStateSubscription() {
        viewModelScope.launch {
            inviteRepository.draft.collectLatest { draft: InviteDraft ->
                updateState { it.copy(companyName = draft.companyName, loaded = true) }
            }
        }
    }

    override fun onAction(action: JoinDataAction) {
        when (action) {
            JoinDataAction.OnBack -> sendEvent(JoinDataEvent.NavigateBack)
            is JoinDataAction.OnConsentChange -> updateState { it.copy(consent = action.value) }
            JoinDataAction.OnContinue -> {
                if (currentState.consent) sendEvent(JoinDataEvent.NavigateToHowItWorks)
            }
        }
    }
}

internal class HowItWorksViewModel(
    private val inviteRepository: InviteRepository,
) : MviViewModel<HowItWorksAction, HowItWorksEvent, HowItWorksState>(HowItWorksState()) {

    override fun onAction(action: HowItWorksAction) {
        when (action) {
            HowItWorksAction.OnFinish -> finish()
        }
    }

    private fun finish() {
        if (currentState.finishing) return
        updateState { it.copy(finishing = true) }
        viewModelScope.launch {
            inviteRepository.markJoined()
            sendEvent(HowItWorksEvent.NavigateToPinSetup)
        }
    }
}
