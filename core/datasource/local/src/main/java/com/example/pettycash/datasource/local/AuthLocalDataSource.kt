package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class AuthLocalDataSource(private val context: Context) {

    private object Keys {
        val PHONE = stringPreferencesKey("phone")
        val PIN = stringPreferencesKey("pin")
        val FINGERPRINT_ENABLED = stringPreferencesKey("fingerprint_enabled")
        val PENDING_PHONE = stringPreferencesKey("pending_phone")
    }

    val phone: Flow<String?> = context.authDataStore.data.map { it[Keys.PHONE] }
    val pin: Flow<String?> = context.authDataStore.data.map { it[Keys.PIN] }
    val fingerprintEnabled: Flow<Boolean> =
        context.authDataStore.data.map { it[Keys.FINGERPRINT_ENABLED]?.toBoolean() ?: false }
    val pendingPhone: Flow<String?> = context.authDataStore.data.map { it[Keys.PENDING_PHONE] }

    suspend fun savePhone(value: String) {
        context.authDataStore.edit { it[Keys.PHONE] = value }
    }

    suspend fun savePin(value: String) {
        context.authDataStore.edit { it[Keys.PIN] = value }
    }

    suspend fun setFingerprintEnabled(value: Boolean) {
        context.authDataStore.edit { it[Keys.FINGERPRINT_ENABLED] = value.toString() }
    }

    suspend fun savePendingPhone(value: String) {
        context.authDataStore.edit { it[Keys.PENDING_PHONE] = value }
    }

    suspend fun pendingPhoneValue(): String? = pendingPhone.first()
    suspend fun pinValue(): String? = pin.first()
    suspend fun phoneValue(): String? = phone.first()
}
