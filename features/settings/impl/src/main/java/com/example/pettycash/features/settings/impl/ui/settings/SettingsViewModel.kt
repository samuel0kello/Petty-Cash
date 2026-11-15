package com.example.pettycash.features.settings.impl.ui.settings

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.settings.impl.data.SettingsRepository
import com.example.pettycash.features.settings.impl.data.SettingsSnapshot
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: SettingsRepository,
) : MviViewModel<ProfileAction, ProfileEvent, ProfileState>(ProfileState()) {

    init {
        repository.snapshot
            .onEach { snapshot -> updateState { it.copy(loading = false, snapshot = snapshot) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.SignOutClick -> updateState { it.copy(confirmSignOut = true) }
            ProfileAction.SignOutDismiss -> updateState { it.copy(confirmSignOut = false) }
            ProfileAction.SignOutConfirm -> {
                updateState { it.copy(confirmSignOut = false) }
                viewModelScope.launch {
                    repository.signOut()
                    sendEvent(ProfileEvent.SignedOut)
                }
            }
        }
    }
}

class AppearanceViewModel(
    private val repository: SettingsRepository,
) : MviViewModel<AppearanceAction, Nothing, AppearanceState>(AppearanceState()) {

    init {
        repository.snapshot
            .onEach { snapshot ->
                updateState {
                    it.copy(
                        loading = false,
                        themeMode = snapshot.themeMode,
                        highContrast = snapshot.highContrast,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AppearanceAction) {
        when (action) {
            is AppearanceAction.ThemeSelected -> {
                updateState { it.copy(themeMode = action.mode) }
                viewModelScope.launch { repository.saveThemeMode(action.mode) }
            }
            is AppearanceAction.HighContrastChanged -> {
                updateState { it.copy(highContrast = action.enabled) }
                viewModelScope.launch { repository.setHighContrast(action.enabled) }
            }
        }
    }
}

class SecurityViewModel(
    private val repository: SettingsRepository,
) : MviViewModel<SecurityAction, Nothing, SecurityState>(SecurityState()) {

    init {
        repository.snapshot
            .onEach { snapshot ->
                updateState {
                    it.copy(loading = false, fingerprintEnabled = snapshot.fingerprintEnabled)
                }
            }
            .launchIn(viewModelScope)
    }

    private var oldPin: String = ""
    private var newPin: String = ""

    override fun onAction(action: SecurityAction) {
        when (action) {
            SecurityAction.ChangePin -> updateState {
                it.copy(pinStep = PinStep.OldPin, pinEntry = "", triesLeft = 5)
            }
            is SecurityAction.PinDigit -> {
                val step = currentState.pinStep
                if (step == PinStep.Hidden || step == PinStep.Done) return
                val entry = (currentState.pinEntry + action.digit).take(4)
                updateState { it.copy(pinEntry = entry) }
                if (entry.length == 4) advancePin(step, entry)
            }
            SecurityAction.PinBackspace -> updateState {
                it.copy(pinEntry = it.pinEntry.dropLast(1))
            }
            SecurityAction.PinBack -> updateState {
                when (it.pinStep) {
                    PinStep.OldPin -> it.copy(pinStep = PinStep.Hidden, pinEntry = "")
                    PinStep.NewPin -> it.copy(pinStep = PinStep.OldPin, pinEntry = "")
                    PinStep.ConfirmPin -> it.copy(pinStep = PinStep.NewPin, pinEntry = "")
                    else -> it
                }
            }
            is SecurityAction.FingerprintChanged -> {
                updateState { it.copy(fingerprintEnabled = action.enabled) }
                viewModelScope.launch { repository.setFingerprintEnabled(action.enabled) }
            }
        }
    }

    private fun advancePin(step: PinStep, entry: String) {
        when (step) {
            PinStep.OldPin -> viewModelScope.launch { verifyOldPin(entry) }
            PinStep.NewPin -> {
                newPin = entry
                updateState { it.copy(pinStep = PinStep.ConfirmPin, pinEntry = "") }
            }
            PinStep.ConfirmPin -> {
                if (entry == newPin) {
                    viewModelScope.launch {
                        repository.changePin(newPin)
                        updateState { it.copy(pinStep = PinStep.Done, pinEntry = "", pinSaved = true) }
                    }
                } else {
                    newPin = ""
                    updateState { it.copy(pinStep = PinStep.NewPin, pinEntry = "") }
                }
            }
            else -> Unit
        }
    }

    private suspend fun verifyOldPin(entry: String) {
        val ok = repository.verifyPin(entry)
        if (ok) {
            updateState { it.copy(pinStep = PinStep.NewPin, pinEntry = "", triesLeft = 5) }
        } else {
            val left = currentState.triesLeft - 1
            updateState {
                it.copy(
                    pinEntry = "",
                    triesLeft = left,
                    pinStep = if (left <= 0) PinStep.Hidden else PinStep.OldPin,
                )
            }
        }
    }
}

class CompanyFormViewModel(
    private val repository: SettingsRepository,
) : MviViewModel<CompanyFormAction, CompanyFormEvent, CompanyFormState>(CompanyFormState()) {

    init {
        repository.snapshot
            .onEach { snapshot ->
                updateState {
                    it.copy(loading = false, profile = snapshot.profile ?: it.profile)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: CompanyFormAction) {
        when (action) {
            is CompanyFormAction.ProfileChanged -> updateState {
                it.copy(profile = action.profile)
            }
            CompanyFormAction.Save -> {
                val profile = currentState.profile ?: return
                updateState { it.copy(saving = true) }
                viewModelScope.launch {
                    repository.saveProfile(profile)
                    updateState { it.copy(saving = false, saved = true) }
                    sendEvent(CompanyFormEvent.Saved)
                }
            }
            CompanyFormAction.DismissSaved -> updateState { it.copy(saved = false) }
        }
    }
}
