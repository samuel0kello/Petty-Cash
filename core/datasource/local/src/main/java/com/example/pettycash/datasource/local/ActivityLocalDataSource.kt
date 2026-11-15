package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.activityDataStore by preferencesDataStore(name = "activity_prefs")

class ActivityLocalDataSource(private val context: Context) {

    private object Keys {
        val SPENDS_JSON = stringPreferencesKey("spends_json")
        val REFUNDS_JSON = stringPreferencesKey("refunds_json")
        val TOPUPS_JSON = stringPreferencesKey("topups_json")
    }

    val spendsJson: Flow<String?> = context.activityDataStore.data.map { it[Keys.SPENDS_JSON] }
    val refundsJson: Flow<String?> = context.activityDataStore.data.map { it[Keys.REFUNDS_JSON] }
    val topupsJson: Flow<String?> = context.activityDataStore.data.map { it[Keys.TOPUPS_JSON] }

    suspend fun saveSpendsJson(value: String) {
        context.activityDataStore.edit { it[Keys.SPENDS_JSON] = value }
    }

    suspend fun saveRefundsJson(value: String) {
        context.activityDataStore.edit { it[Keys.REFUNDS_JSON] = value }
    }

    suspend fun saveTopupsJson(value: String) {
        context.activityDataStore.edit { it[Keys.TOPUPS_JSON] = value }
    }
}
