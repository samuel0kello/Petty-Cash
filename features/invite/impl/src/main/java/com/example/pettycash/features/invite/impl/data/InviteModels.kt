package com.example.pettycash.features.invite.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class TeamMember(
    val id: String,
    val name: String,
    val role: String,
    val isSelf: Boolean = false,
)

@Serializable
data class PendingInvite(
    val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val invitedBy: String = "",
    val status: String = "pending",
)

@Serializable
data class MemberProfile(
    val name: String = "",
    val phone: String = "",
    val companyName: String = "",
)

enum class InvitePreviewStatus { Valid, Expired, WrongNumber }
