package com.example.pettycash.features.invite.impl.data

import kotlinx.coroutines.flow.Flow

data class InviteDraft(
    val name: String = "",
    val phone: String = "",
    val companyName: String = "",
)

interface InviteRepository {
    val team: Flow<TeamSnapshot>
    val draft: Flow<InviteDraft>
    suspend fun sendInvite(name: String, phone: String, approver: Boolean, custodian: Boolean)
    suspend fun inviteById(id: String): PendingInvite?
    suspend fun beginJoin(inviteId: String)
    suspend fun updateDraft(name: String, phone: String)
    suspend fun markJoined()
}

data class TeamSnapshot(
    val companyName: String,
    val members: List<TeamMember>,
    val invites: List<PendingInvite>,
)
