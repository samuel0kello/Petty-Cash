package com.example.pettycash.features.auth.impl.phonenumber

internal data class PhoneNumberState(
    val phone: String = "",
    val isError: Boolean = false,
    val isSubmitting: Boolean = false,
)

internal sealed interface PhoneNumberAction {
    data class OnPhoneChange(val value: String) : PhoneNumberAction
    data object OnContinue : PhoneNumberAction
}

internal sealed interface PhoneNumberEvent {
    data object NavigateToSmsCode : PhoneNumberEvent
    data object ShowSmsPermissionNote : PhoneNumberEvent
}
