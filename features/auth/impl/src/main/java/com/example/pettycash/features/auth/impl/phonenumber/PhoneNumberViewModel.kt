package com.example.pettycash.features.auth.impl.phonenumber

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.auth.impl.data.AuthRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.launch

internal class PhoneNumberViewModel(
    private val authRepository: AuthRepository,
) : MviViewModel<PhoneNumberAction, PhoneNumberEvent, PhoneNumberState>(PhoneNumberState()) {

    override fun onAction(action: PhoneNumberAction) {
        when (action) {
            is PhoneNumberAction.OnPhoneChange -> updateState {
                it.copy(phone = action.value.filter(Char::isDigit).take(10), isError = false)
            }
            PhoneNumberAction.OnContinue -> submit()
        }
    }

    private fun submit() {
        val normalized = normalizeKenyanPhone(currentState.phone) ?: run {
            updateState { it.copy(isError = true) }
            return
        }
        if (currentState.isSubmitting) return
        updateState { it.copy(isSubmitting = true, isError = false) }
        viewModelScope.launch {
            authRepository.requestCode(normalized)
                .onSuccess { sendEvent(PhoneNumberEvent.NavigateToSmsCode) }
                .onFailure {
                    updateState { it.copy(isSubmitting = false, isError = true) }
                }
        }
    }

    companion object {
        fun normalizeKenyanPhone(raw: String): String? {
            val digits = raw.filter(Char::isDigit)
            val national = when {
                digits.startsWith("254") && digits.length == 12 -> digits.drop(3)
                digits.startsWith("0") && digits.length == 10 -> digits.drop(1)
                digits.length == 9 -> digits
                else -> return null
            }
            return if (national.matches(Regex("7\\d{8}|1\\d{8}"))) "254$national" else null
        }

        fun formatForDisplay(e164: String): String {
            val national = e164.removePrefix("254")
            return "0$national"
        }
    }
}
