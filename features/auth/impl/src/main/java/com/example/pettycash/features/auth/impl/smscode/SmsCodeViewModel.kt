package com.example.pettycash.features.auth.impl.smscode

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.auth.impl.data.AuthRepository
import com.example.pettycash.features.auth.impl.data.LocalAuthRepository
import com.example.pettycash.features.auth.impl.phonenumber.PhoneNumberViewModel
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class SmsCodeViewModel(
    private val authRepository: AuthRepository,
) : MviViewModel<SmsCodeAction, SmsCodeEvent, SmsCodeState>(SmsCodeState()) {

    init {
        viewModelScope.launch {
            authRepository.pendingPhone()?.let { phone ->
                updateState {
                    it.copy(phoneDisplay = PhoneNumberViewModel.formatForDisplay(phone))
                }
            }
        }
        startResendTimer(RESEND_SECONDS)
    }

    override fun onAction(action: SmsCodeAction) {
        when (action) {
            is SmsCodeAction.OnCodeChange -> updateState {
                it.copy(
                    code = action.value.filter(Char::isDigit).take(6),
                    isError = false,
                    errorMessage = null,
                )
            }
            SmsCodeAction.OnVerify -> verify()
            SmsCodeAction.OnResend -> resend()
            SmsCodeAction.OnChangeNumber -> sendEvent(SmsCodeEvent.GoBack)
            SmsCodeAction.OnBlockedDismiss -> Unit
        }
    }

    private fun verify() {
        val code = currentState.code
        if (code.length != 6 || currentState.isVerifying || currentState.isBlocked) return
        updateState { it.copy(isVerifying = true) }
        viewModelScope.launch {
            authRepository.verifyCode(code)
                .onSuccess { isNewUser ->
                    updateState { it.copy(isVerifying = false) }
                    val goToCreatePin = authRepository.consumePinReset() || isNewUser
                    sendEvent(
                        if (goToCreatePin) SmsCodeEvent.NavigateToCreatePin else SmsCodeEvent.NavigateToUnlock,
                    )
                }
                .onFailure { error ->
                    when (error) {
                        is LocalAuthRepository.BlockedException -> updateState {
                            it.copy(isVerifying = false, isBlocked = true)
                        }
                        is LocalAuthRepository.WrongCodeException -> updateState {
                            it.copy(
                                isVerifying = false,
                                isError = true,
                                triesLeft = error.triesLeft,
                                errorMessage = "wrong",
                            )
                        }
                        else -> updateState {
                            it.copy(isVerifying = false, isError = true, errorMessage = "expired")
                        }
                    }
                }
        }
    }

    private fun resend() {
        if (currentState.resendInSeconds > 0) return
        viewModelScope.launch {
            authRepository.resendCode()
            startResendTimer(RESEND_SECONDS)
        }
    }

    private fun startResendTimer(seconds: Int) {
        viewModelScope.launch {
            var remaining = seconds
            updateState { it.copy(resendInSeconds = remaining) }
            while (remaining > 0) {
                delay(1000)
                remaining--
                updateState { it.copy(resendInSeconds = remaining) }
            }
        }
    }

    private companion object {
        const val RESEND_SECONDS = 45
    }
}
