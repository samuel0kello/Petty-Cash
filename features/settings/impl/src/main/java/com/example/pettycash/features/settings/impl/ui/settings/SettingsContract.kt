package com.example.pettycash.features.settings.impl.ui.settings

import com.example.pettycash.features.settings.impl.data.SettingsProfile
import com.example.pettycash.features.settings.impl.data.SettingsSnapshot

data class ProfileState(
    val loading: Boolean = true,
    val snapshot: SettingsSnapshot? = null,
    val confirmSignOut: Boolean = false,
)

sealed interface ProfileAction {
    data object SignOutClick : ProfileAction
    data object SignOutConfirm : ProfileAction
    data object SignOutDismiss : ProfileAction
}

sealed interface ProfileEvent {
    data object SignedOut : ProfileEvent
}

data class AppearanceState(
    val loading: Boolean = true,
    val themeMode: String = "system",
    val highContrast: Boolean = false,
)

sealed interface AppearanceAction {
    data class ThemeSelected(val mode: String) : AppearanceAction
    data class HighContrastChanged(val enabled: Boolean) : AppearanceAction
}

data class SecurityState(
    val loading: Boolean = true,
    val pinStep: PinStep = PinStep.Hidden,
    val pinEntry: String = "",
    val triesLeft: Int = 5,
    val fingerprintEnabled: Boolean = false,
    val pinSaved: Boolean = false,
)

enum class PinStep { Hidden, OldPin, NewPin, ConfirmPin, Done }

sealed interface SecurityAction {
    data object ChangePin : SecurityAction
    data class PinDigit(val digit: String) : SecurityAction
    data object PinBackspace : SecurityAction
    data object PinBack : SecurityAction
    data class FingerprintChanged(val enabled: Boolean) : SecurityAction
}

data class CompanyFormState(
    val loading: Boolean = true,
    val profile: SettingsProfile? = null,
    val saving: Boolean = false,
    val saved: Boolean = false,
)

sealed interface CompanyFormAction {
    data class ProfileChanged(val profile: SettingsProfile) : CompanyFormAction
    data object Save : CompanyFormAction
    data object DismissSaved : CompanyFormAction
}

sealed interface CompanyFormEvent {
    data object Saved : CompanyFormEvent
}
