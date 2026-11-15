package com.example.pettycash.features.settings.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.features.settings.impl.data.SettingsSnapshot
import com.example.pettycash.features.settings.impl.ui.settings.MoreScreen
import com.example.pettycash.features.settings.impl.ui.settings.ProfileScreen
import com.example.pettycash.features.settings.impl.ui.settings.ProfileState
import com.example.pettycash.mai.theme.MaiTheme

@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

private fun memberSnapshot() = SettingsSnapshot(
    profile = null,
    userName = "Brian Otieno",
    userPhone = "+254 712 345 678",
    memberPhone = "",
    role = "Member",
    themeMode = "system",
    highContrast = false,
    fingerprintEnabled = true,
    people = emptyList(),
)

private fun adminSnapshot() = memberSnapshot().copy(
    userName = "Grace Wanjiku",
    userPhone = "+254 733 444 555",
    role = "Admin",
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Dark member profile")
@Composable
fun DarkMemberProfileScreenshot() {
    MaiTheme(darkTheme = true) {
        PreviewDevice {
            ProfileScreen(
                state = ProfileState(loading = false, snapshot = memberSnapshot()),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Dark admin more")
@Composable
fun DarkAdminMoreScreenshot() {
    MaiTheme(darkTheme = true) {
        PreviewDevice {
            MoreScreen(snapshot = adminSnapshot())
        }
    }
}
