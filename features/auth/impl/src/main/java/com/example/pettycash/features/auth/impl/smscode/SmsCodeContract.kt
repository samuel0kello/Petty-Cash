package com.example.pettycash.features.auth.impl.smscode

internal data class SmsCodeState(
    val phoneDisplay: String = "",
    val code: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val triesLeft: Int = 3,
    val isBlocked: Boolean = false,
    val resendInSeconds: Int = 0,
    val isVerifying: Boolean = false,
)

internal sealed interface SmsCodeAction {
    data class OnCodeChange(val value: String) : SmsCodeAction
    data object OnVerify : SmsCodeAction
    data object OnResend : SmsCodeAction
    data object OnChangeNumber : SmsCodeAction
    data object OnBlockedDismiss : SmsCodeAction
}

internal sealed interface SmsCodeEvent {
    data object NavigateToCreatePin : SmsCodeEvent
    data object NavigateToUnlock : SmsCodeEvent
    data object GoBack : SmsCodeEvent
}
