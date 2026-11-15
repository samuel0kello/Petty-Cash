package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.inviteDataStore by preferencesDataStore(name = "invite_prefs")

class InviteLocalDataSource(private val context: Context) {

    private object Keys {
        val MEMBERS_JSON = stringPreferencesKey("team_members_json")
        val INVITES_JSON = stringPreferencesKey("pending_invites_json")
        val MEMBER_NAME = stringPreferencesKey("member_name")
        val MEMBER_PHONE = stringPreferencesKey("member_phone")
        val COMPANY_NAME = stringPreferencesKey("member_company_name")
        val COACH_MARK_PENDING = booleanPreferencesKey("member_coach_mark_pending")
    }

    val membersJson: Flow<String?> = context.inviteDataStore.data.map { it[Keys.MEMBERS_JSON] }
    val invitesJson: Flow<String?> = context.inviteDataStore.data.map { it[Keys.INVITES_JSON] }
    val memberName: Flow<String?> = context.inviteDataStore.data.map { it[Keys.MEMBER_NAME] }
    val memberPhone: Flow<String?> = context.inviteDataStore.data.map { it[Keys.MEMBER_PHONE] }
    val companyName: Flow<String?> = context.inviteDataStore.data.map { it[Keys.COMPANY_NAME] }
    val coachMarkPending: Flow<Boolean> =
        context.inviteDataStore.data.map { it[Keys.COACH_MARK_PENDING] ?: false }

    suspend fun saveMembersJson(value: String) {
        context.inviteDataStore.edit { it[Keys.MEMBERS_JSON] = value }
    }

    suspend fun saveInvitesJson(value: String) {
        context.inviteDataStore.edit { it[Keys.INVITES_JSON] = value }
    }

    suspend fun saveMemberProfile(name: String, phone: String, company: String) {
        context.inviteDataStore.edit {
            it[Keys.MEMBER_NAME] = name
            it[Keys.MEMBER_PHONE] = phone
            it[Keys.COMPANY_NAME] = company
        }
    }

    suspend fun markCoachMarkPending() {
        context.inviteDataStore.edit { it[Keys.COACH_MARK_PENDING] = true }
    }

    suspend fun clearCoachMarkPending() {
        context.inviteDataStore.edit { it[Keys.COACH_MARK_PENDING] = false }
    }
}
