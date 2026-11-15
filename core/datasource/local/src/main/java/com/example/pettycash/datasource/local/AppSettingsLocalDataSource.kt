package com.example.pettycash.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppSettingsLocalDataSource(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    }

    val themeMode: Flow<String> =
        context.appSettingsDataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val highContrast: Flow<Boolean> =
        context.appSettingsDataStore.data.map { it[Keys.HIGH_CONTRAST] ?: false }

    suspend fun saveThemeMode(value: String) {
        context.appSettingsDataStore.edit { it[Keys.THEME_MODE] = value }
    }

    suspend fun setHighContrast(value: Boolean) {
        context.appSettingsDataStore.edit { it[Keys.HIGH_CONTRAST] = value }
    }

    suspend fun themeModeValue(): String = themeMode.first()
}
