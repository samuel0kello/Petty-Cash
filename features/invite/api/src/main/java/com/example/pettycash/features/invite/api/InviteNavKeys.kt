package com.example.pettycash.features.invite.api

import androidx.navigation3.runtime.NavKey

data object TeamNavKey : NavKey
data object InvitePeopleNavKey : NavKey
data object InvitesSentNavKey : NavKey
data class InvitePreviewNavKey(val inviteId: String) : NavKey
data object JoinDetailsNavKey : NavKey
data object JoinDataNavKey : NavKey
data object HowItWorksNavKey : NavKey
