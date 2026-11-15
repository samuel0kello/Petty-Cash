package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.companyDataStore by preferencesDataStore(name = "company_prefs")

class CompanyLocalDataSource(private val context: Context) {

    private object Keys {
        val PROFILE_JSON = stringPreferencesKey("profile_json")
        val SETUP_COMPLETE = stringPreferencesKey("setup_complete")
    }

    val profileJson: Flow<String?> = context.companyDataStore.data.map { it[Keys.PROFILE_JSON] }
    val setupComplete: Flow<Boolean> =
        context.companyDataStore.data.map { it[Keys.SETUP_COMPLETE]?.toBoolean() ?: false }

    suspend fun saveProfileJson(value: String) {
        context.companyDataStore.edit { it[Keys.PROFILE_JSON] = value }
    }

    suspend fun markSetupComplete() {
        context.companyDataStore.edit { it[Keys.SETUP_COMPLETE] = "true" }
    }

    suspend fun profileJsonValue(): String? = profileJson.first()
    suspend fun setupCompleteValue(): Boolean = setupComplete.first()
}
