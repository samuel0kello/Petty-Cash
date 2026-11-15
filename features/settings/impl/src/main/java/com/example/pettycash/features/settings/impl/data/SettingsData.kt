package com.example.pettycash.features.settings.impl.data

import com.example.pettycash.datasource.local.AppSettingsLocalDataSource
import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class SettingsProfile(
    val companyName: String = "",
    val industry: String? = null,
    val teamSize: String? = null,
    val kraPin: String = "",
    val businessReg: String = "",
    val mpesaType: String = "Paybill",
    val mpesaNumber: String = "",
    val mpesaAccountName: String = "",
    val mpesaConnected: Boolean = false,
    val floatName: String = "",
    val floatStartingAmount: String = "",
    val floatCustodian: String = "",
    val floatSingleSpendLimit: String = "",
    val floatReceiptDeadline: String = "",
    val floatNoReceiptLimit: String = "",
    val approverRule: String = "Admins",
    val categories: List<String> = emptyList(),
    val projects: List<String> = emptyList(),
    val sites: List<String> = emptyList(),
    val voucherStartFresh: Boolean = true,
    val lastVoucherNumber: String = "",
)

data class TeamPerson(
    val id: String,
    val name: String,
    val role: String,
    val isSelf: Boolean,
)

data class SettingsSnapshot(
    val profile: SettingsProfile?,
    val userName: String,
    val userPhone: String,
    val memberPhone: String,
    val role: String,
    val themeMode: String,
    val highContrast: Boolean,
    val fingerprintEnabled: Boolean,
    val people: List<TeamPerson>,
)

interface SettingsRepository {
    val snapshot: Flow<SettingsSnapshot>
    suspend fun saveProfile(profile: SettingsProfile)
    suspend fun saveThemeMode(mode: String)
    suspend fun setHighContrast(enabled: Boolean)
    suspend fun setFingerprintEnabled(enabled: Boolean)
    suspend fun changePin(newPin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun removePerson(id: String)
    suspend fun signOut()
}

class LocalSettingsRepository(
    private val companyLocal: CompanyLocalDataSource,
    private val authLocal: AuthLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val homeLocal: HomeLocalDataSource,
    private val appSettings: AppSettingsLocalDataSource,
    private val json: Json,
) : SettingsRepository {

    override val snapshot: Flow<SettingsSnapshot> = combine(
        companyLocal.profileJson,
        authLocal.phone,
        inviteLocal.memberName,
        inviteLocal.memberPhone,
        inviteLocal.membersJson,
        homeLocal.selectedRole,
        appSettings.themeMode,
        appSettings.highContrast,
        authLocal.fingerprintEnabled,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val profile = (values[0] as String?)?.let {
            runCatching { json.decodeFromString(SettingsProfile.serializer(), it) }.getOrNull()
        }
        val members = (values[4] as String?)?.let {
            runCatching {
                json.decodeFromString(ListSerializer(MemberRow.serializer()), it)
            }.getOrDefault(emptyList())
        } ?: emptyList()
        SettingsSnapshot(
            profile = profile,
            userName = values[2] as String? ?: "",
            userPhone = values[1] as String? ?: "",
            memberPhone = values[3] as String? ?: "",
            role = values[5] as String? ?: "Member",
            themeMode = values[6] as String,
            highContrast = values[7] as Boolean,
            fingerprintEnabled = values[8] as Boolean,
            people = members.map {
                TeamPerson(it.id, it.name, it.role, it.isSelf)
            },
        )
    }

    override suspend fun saveProfile(profile: SettingsProfile) {
        companyLocal.saveProfileJson(
            json.encodeToString(
                SettingsProfile.serializer(),
                profile.copy(mpesaConnected = profile.mpesaConnected || profile.mpesaNumber.isNotBlank()),
            ),
        )
    }

    override suspend fun saveThemeMode(mode: String) = appSettings.saveThemeMode(mode)

    override suspend fun setHighContrast(enabled: Boolean) = appSettings.setHighContrast(enabled)

    override suspend fun setFingerprintEnabled(enabled: Boolean) =
        authLocal.setFingerprintEnabled(enabled)

    override suspend fun changePin(newPin: String) = authLocal.savePin(newPin)

    override suspend fun verifyPin(pin: String): Boolean = authLocal.pinValue() == pin

    override suspend fun removePerson(id: String) {
        val raw = inviteLocal.membersJson.first() ?: return
        val members = runCatching {
            json.decodeFromString(ListSerializer(MemberRow.serializer()), raw)
        }.getOrDefault(emptyList())
        inviteLocal.saveMembersJson(
            json.encodeToString(
                ListSerializer(MemberRow.serializer()),
                members.filter { it.id != id },
            ),
        )
    }

    override suspend fun signOut() {
        authLocal.savePhone("")
        authLocal.savePin("")
        authLocal.setFingerprintEnabled(false)
    }

    @Serializable
    private data class MemberRow(
        val id: String = "",
        val name: String = "",
        val role: String = "",
        val isSelf: Boolean = false,
    )
}
