package com.example.pettycash.features.invite.impl.data

import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class LocalInviteRepository(
    private val local: InviteLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val homeLocal: HomeLocalDataSource,
) : InviteRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private var seeded = false
    private var draftName = ""
    private var draftPhone = ""

    override val team: Flow<TeamSnapshot> = flow {
        ensureSeeded()
        combine(local.membersJson, local.invitesJson) { members, invites ->
            TeamSnapshot(
                companyName = companyName(),
                members = members?.let(::decodeMembers).orEmpty().ifEmpty { seedMembers() },
                invites = invites?.let(::decodeInvites).orEmpty().ifEmpty { seedInvites() },
            )
        }.collect { emit(it) }
    }

    override val draft: Flow<InviteDraft> = flow {
        emit(InviteDraft(name = draftName, phone = draftPhone, companyName = companyName()))
    }

    override suspend fun sendInvite(name: String, phone: String, approver: Boolean, custodian: Boolean) {
        val role = buildString {
            append("Member")
            if (approver) append(" · Approver")
            if (custodian) append(" · Custodian")
        }
        val invite = PendingInvite(
            id = "inv-${System.currentTimeMillis()}",
            name = name,
            phone = phone,
            role = role,
            invitedBy = "You",
        )
        val invites = currentInvites().toMutableList()
        invites.add(0, invite)
        local.saveInvitesJson(json.encodeToString(ListSerializer(PendingInvite.serializer()), invites))
    }

    override suspend fun inviteById(id: String): PendingInvite? {
        ensureSeeded()
        return currentInvites().firstOrNull { it.id == id }
    }

    override suspend fun beginJoin(inviteId: String) {
        val invite = inviteById(inviteId) ?: return
        draftPhone = invite.phone
    }

    override suspend fun updateDraft(name: String, phone: String) {
        draftName = name
        draftPhone = phone
    }

    override suspend fun markJoined() {
        local.saveMemberProfile(draftName, draftPhone, companyName())
        homeLocal.markCoachMarkPending()
        homeLocal.saveSelectedRole("Member")
    }

    private suspend fun ensureSeeded() {
        if (seeded) return
        seeded = true
        if (local.membersJson.first() == null) {
            local.saveMembersJson(
                json.encodeToString(ListSerializer(TeamMember.serializer()), seedMembers()),
            )
            local.saveInvitesJson(
                json.encodeToString(ListSerializer(PendingInvite.serializer()), seedInvites()),
            )
        }
    }

    private suspend fun currentInvites(): List<PendingInvite> {
        ensureSeeded()
        val raw = local.invitesJson.first()
        return raw?.let(::decodeInvites).orEmpty().ifEmpty { seedInvites() }
    }

    private suspend fun companyName(): String {
        val raw = companyLocal.profileJson.first() ?: return DEFAULT_COMPANY
        return runCatching {
            json.decodeFromString(CompanyNameDto.serializer(), raw).companyName
        }.getOrDefault("").ifBlank { DEFAULT_COMPANY }
    }

    private fun decodeMembers(raw: String): List<TeamMember> =
        json.decodeFromString(ListSerializer(TeamMember.serializer()), raw)

    private fun decodeInvites(raw: String): List<PendingInvite> =
        json.decodeFromString(ListSerializer(PendingInvite.serializer()), raw)

    private fun seedMembers() = listOf(
        TeamMember("m1", "Amara Okello", "Admin", isSelf = true),
        TeamMember("m2", "Grace Wanjiku", "Approver"),
        TeamMember("m3", "Brian Otieno", "Member"),
    )

    private fun seedInvites() = listOf(
        PendingInvite("inv-1", "Kevin Mwangi", "+254 722 111 222", "Member", invitedBy = "Amara"),
        PendingInvite("inv-2", "Susan Achieng", "+254 733 444 555", "Custodian", invitedBy = "Amara"),
    )

    @Serializable
    private data class CompanyNameDto(val companyName: String = "")

    private companion object {
        const val DEFAULT_COMPANY = "Acme Traders"
    }
}
