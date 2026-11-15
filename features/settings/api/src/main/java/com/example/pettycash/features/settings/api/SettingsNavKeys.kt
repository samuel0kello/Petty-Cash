package com.example.pettycash.features.settings.api

import androidx.navigation3.runtime.NavKey

data object SettingsAppearanceNavKey : NavKey
data object SettingsSecurityNavKey : NavKey
data object SettingsChangePinNavKey : NavKey
data object SettingsLimitsNavKey : NavKey
data object SettingsProjectsNavKey : NavKey
data object SettingsVouchersNavKey : NavKey
data object SettingsMpesaNavKey : NavKey
data object SettingsCompanyNavKey : NavKey
data class SettingsPersonNavKey(val name: String) : NavKey
