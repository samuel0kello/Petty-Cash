package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.requestDataStore by preferencesDataStore(name = "request_prefs")

class RequestLocalDataSource(private val context: Context) {

    private object Keys {
        val REQUESTS_JSON = stringPreferencesKey("cash_requests_json")
    }

    val requestsJson: Flow<String?> = context.requestDataStore.data.map { it[Keys.REQUESTS_JSON] }

    suspend fun saveRequestsJson(value: String) {
        context.requestDataStore.edit { it[Keys.REQUESTS_JSON] = value }
    }
}
