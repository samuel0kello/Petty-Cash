package com.example.pettycash.features.invite.impl.ui.join

data class JoinDetailsState(
    val name: String = "",
    val phone: String = "",
    val companyName: String = "",
    val loaded: Boolean = false,
)

sealed interface JoinDetailsAction {
    data object OnBack : JoinDetailsAction
    data class OnNameChange(val value: String) : JoinDetailsAction
    data class OnPhoneChange(val value: String) : JoinDetailsAction
    data object OnContinue : JoinDetailsAction
}

sealed interface JoinDetailsEvent {
    data object NavigateBack : JoinDetailsEvent
    data object NavigateToData : JoinDetailsEvent
}

data class JoinDataState(
    val companyName: String = "",
    val consent: Boolean = false,
    val loaded: Boolean = false,
)

sealed interface JoinDataAction {
    data object OnBack : JoinDataAction
    data class OnConsentChange(val value: Boolean) : JoinDataAction
    data object OnContinue : JoinDataAction
}

sealed interface JoinDataEvent {
    data object NavigateBack : JoinDataEvent
    data object NavigateToHowItWorks : JoinDataEvent
}

data class HowItWorksState(
    val finishing: Boolean = false,
)

sealed interface HowItWorksAction {
    data object OnFinish : HowItWorksAction
}

sealed interface HowItWorksEvent {
    data object NavigateToPinSetup : HowItWorksEvent
}
