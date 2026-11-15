package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.homeDataStore by preferencesDataStore(name = "home_prefs")

class HomeLocalDataSource(private val context: Context) {

    private object Keys {
        val SELECTED_ROLE = stringPreferencesKey("selected_role")
        val FIRST_VISIT_PENDING = booleanPreferencesKey("first_visit_pending")
        val COACH_MARK_PENDING = booleanPreferencesKey("member_coach_mark_pending")
    }

    val selectedRole: Flow<String?> = context.homeDataStore.data.map { it[Keys.SELECTED_ROLE] }
    val firstVisitPending: Flow<Boolean> =
        context.homeDataStore.data.map { it[Keys.FIRST_VISIT_PENDING] ?: true }
    val coachMarkPending: Flow<Boolean> =
        context.homeDataStore.data.map { it[Keys.COACH_MARK_PENDING] ?: false }

    suspend fun saveSelectedRole(value: String) {
        context.homeDataStore.edit { it[Keys.SELECTED_ROLE] = value }
    }

    suspend fun clearFirstVisitPending() {
        context.homeDataStore.edit { it[Keys.FIRST_VISIT_PENDING] = false }
    }

    suspend fun markCoachMarkPending() {
        context.homeDataStore.edit { it[Keys.COACH_MARK_PENDING] = true }
    }

    suspend fun clearCoachMarkPending() {
        context.homeDataStore.edit { it[Keys.COACH_MARK_PENDING] = false }
    }

    suspend fun selectedRoleValue(): String? = selectedRole.first()
}
