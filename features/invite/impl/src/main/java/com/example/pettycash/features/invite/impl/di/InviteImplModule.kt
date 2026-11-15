package com.example.pettycash.features.invite.impl.di

import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.auth.api.AuthCreatePinNavKey
import com.example.pettycash.features.invite.api.HowItWorksNavKey
import com.example.pettycash.features.invite.api.InvitePeopleNavKey
import com.example.pettycash.features.invite.api.InvitePreviewNavKey
import com.example.pettycash.features.invite.api.InvitesSentNavKey
import com.example.pettycash.features.invite.api.JoinDataNavKey
import com.example.pettycash.features.invite.api.JoinDetailsNavKey
import com.example.pettycash.features.invite.api.TeamNavKey
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.features.invite.impl.data.LocalInviteRepository
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormRoute
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormViewModel
import com.example.pettycash.features.invite.impl.ui.invite.InvitesSentRoute
import com.example.pettycash.features.invite.impl.ui.join.HowItWorksRoute
import com.example.pettycash.features.invite.impl.ui.join.HowItWorksViewModel
import com.example.pettycash.features.invite.impl.ui.join.JoinDataRoute
import com.example.pettycash.features.invite.impl.ui.join.JoinDataViewModel
import com.example.pettycash.features.invite.impl.ui.join.JoinDetailsRoute
import com.example.pettycash.features.invite.impl.ui.join.JoinDetailsViewModel
import com.example.pettycash.features.invite.impl.ui.preview.InvitePreviewRoute
import com.example.pettycash.features.invite.impl.ui.preview.InvitePreviewViewModel
import com.example.pettycash.features.invite.impl.ui.team.TeamEvent
import com.example.pettycash.features.invite.impl.ui.team.TeamRoute
import com.example.pettycash.features.invite.impl.ui.team.TeamViewModel
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormEvent
import com.example.pettycash.features.invite.impl.ui.join.JoinDataEvent
import com.example.pettycash.features.invite.impl.ui.join.JoinDetailsEvent
import com.example.pettycash.features.invite.impl.ui.join.HowItWorksEvent
import com.example.pettycash.features.invite.impl.ui.preview.InvitePreviewEvent
import com.example.pettycash.presentation.LocalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val InviteImplModule = module {
    single { InviteLocalDataSource(androidContext()) }
    single<InviteRepository> { LocalInviteRepository(get(), get(), get()) }

    viewModelOf(::TeamViewModel)
    viewModelOf(::InviteFormViewModel)
    viewModel { params -> InvitePreviewViewModel(get(), params.get()) }
    viewModelOf(::JoinDetailsViewModel)
    viewModelOf(::JoinDataViewModel)
    viewModelOf(::HowItWorksViewModel)

    navigation<TeamNavKey> {
        val navigator = LocalNavigator.current
        TeamRoute(
            onTeamEvent = { event ->
                when (event) {
                    TeamEvent.NavigateBack -> navigator.goBack()
                    TeamEvent.NavigateToInviteForm -> navigator.goTo(InvitePeopleNavKey)
                    is TeamEvent.NavigateToInvitePreview ->
                        navigator.goTo(InvitePreviewNavKey(event.inviteId))
                }
            },
        )
    }

    navigation<InvitePeopleNavKey> {
        val navigator = LocalNavigator.current
        InviteFormRoute(
            onInviteFormEvent = { event ->
                when (event) {
                    InviteFormEvent.NavigateBack -> navigator.goBack()
                    InviteFormEvent.NavigateToSent -> navigator.goTo(InvitesSentNavKey)
                }
            },
        )
    }

    navigation<InvitesSentNavKey> {
        val navigator = LocalNavigator.current
        InvitesSentRoute(onDone = { navigator.goBack() })
    }

    navigation<InvitePreviewNavKey> { key ->
        val navigator = LocalNavigator.current
        InvitePreviewRoute(
            inviteId = key.inviteId,
            onInvitePreviewEvent = { event ->
                when (event) {
                    InvitePreviewEvent.NavigateToJoinDetails -> navigator.goTo(JoinDetailsNavKey)
                    InvitePreviewEvent.NavigateBack -> navigator.goBack()
                }
            },
        )
    }

    navigation<JoinDetailsNavKey> {
        val navigator = LocalNavigator.current
        JoinDetailsRoute(
            onJoinDetailsEvent = { event ->
                when (event) {
                    JoinDetailsEvent.NavigateBack -> navigator.goBack()
                    JoinDetailsEvent.NavigateToData -> navigator.goTo(JoinDataNavKey)
                }
            },
        )
    }

    navigation<JoinDataNavKey> {
        val navigator = LocalNavigator.current
        JoinDataRoute(
            onJoinDataEvent = { event ->
                when (event) {
                    JoinDataEvent.NavigateBack -> navigator.goBack()
                    JoinDataEvent.NavigateToHowItWorks -> navigator.goTo(HowItWorksNavKey)
                }
            },
        )
    }

    navigation<HowItWorksNavKey> {
        val navigator = LocalNavigator.current
        HowItWorksRoute(
            onHowItWorksEvent = { event ->
                when (event) {
                    HowItWorksEvent.NavigateToPinSetup -> navigator.goTo(AuthCreatePinNavKey)
                }
            },
        )
    }
}
