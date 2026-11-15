package com.example.pettycash.features.auth.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.features.auth.impl.confirmpin.ConfirmPinScreen
import com.example.pettycash.features.auth.impl.confirmpin.ConfirmPinState
import com.example.pettycash.features.auth.impl.createpin.CreatePinScreen
import com.example.pettycash.features.auth.impl.createpin.CreatePinState
import com.example.pettycash.features.auth.impl.fingerprint.FingerprintScreen
import com.example.pettycash.features.auth.impl.newphone.NewPhoneScreen
import com.example.pettycash.features.auth.impl.phonenumber.PhoneNumberScreen
import com.example.pettycash.features.auth.impl.phonenumber.PhoneNumberState
import com.example.pettycash.features.auth.impl.smscode.SmsCodeScreen
import com.example.pettycash.features.auth.impl.smscode.SmsCodeState
import com.example.pettycash.features.auth.impl.unlock.UnlockScreen
import com.example.pettycash.features.auth.impl.unlock.UnlockState
import com.example.pettycash.mai.theme.MaiTheme


@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Phone number")
@Composable
fun PhoneNumberScreenshot() {
    MaiTheme {
        PreviewDevice {
        PhoneNumberScreen(
                state = PhoneNumberState(phone = "712 345 678"),
                onAction = {},
                onBack = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "SMS code")
@Composable
fun SmsCodeScreenshot() {
    MaiTheme {
        PreviewDevice {
        SmsCodeScreen(
                state = SmsCodeState(phoneDisplay = "0712 345 678", code = "482", resendInSeconds = 45),
                onAction = {},
                onBack = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Create PIN")
@Composable
fun CreatePinScreenshot() {
    MaiTheme {
        PreviewDevice {
        CreatePinScreen(
                state = CreatePinState(entered = 2, digits = "12"),
                onAction = {},
                onBack = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Confirm PIN error")
@Composable
fun ConfirmPinScreenshot() {
    MaiTheme {
        PreviewDevice {
        ConfirmPinScreen(
                state = ConfirmPinState(),
                onAction = {},
                onBack = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Fingerprint offer")
@Composable
fun FingerprintScreenshot() {
    MaiTheme {
        PreviewDevice {
        FingerprintScreen(onTurnOn = {}, onNotNow = {}, onBack = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Unlock")
@Composable
fun UnlockScreenshot() {
    MaiTheme {
        PreviewDevice {
        UnlockScreen(state = UnlockState(), onAction = {}, onBack = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "New phone")
@Composable
fun NewPhoneScreenshot() {
    MaiTheme {
        PreviewDevice {
        NewPhoneScreen(onContinue = {}, onBack = {})
        }
    }
}
