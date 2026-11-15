package com.example.pettycash.features.home.impl

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
import com.example.pettycash.features.home.impl.data.adminDashboard
import com.example.pettycash.features.home.impl.data.custodianDashboard
import com.example.pettycash.features.home.impl.data.memberDashboard
import com.example.pettycash.features.home.impl.ui.HomeScreen
import com.example.pettycash.features.home.impl.ui.HomeState
import com.example.pettycash.mai.theme.MaiTheme

@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 991, name = "Admin home")
@Composable
fun AdminHomeScreenshot() {
    MaiTheme {
        PreviewDevice {
            HomeScreen(
                state = HomeState(dashboard = adminDashboard(firstVisit = false)),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Member home")
@Composable
fun MemberHomeScreenshot() {
    MaiTheme {
        PreviewDevice {
            HomeScreen(
                state = HomeState(dashboard = memberDashboard()),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Custodian home")
@Composable
fun CustodianHomeScreenshot() {
    MaiTheme {
        PreviewDevice {
            HomeScreen(
                state = HomeState(dashboard = custodianDashboard()),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 991, name = "Role switcher")
@Composable
fun RoleSwitcherScreenshot() {
    MaiTheme {
        PreviewDevice {
            HomeScreen(
                state = HomeState(dashboard = adminDashboard(firstVisit = false), roleSwitcherOpen = true),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 993, name = "Admin home first visit")
@Composable
fun FirstVisitHomeScreenshot() {
    MaiTheme {
        PreviewDevice {
            HomeScreen(
                state = HomeState(dashboard = adminDashboard(firstVisit = true)),
                onAction = {},
            )
        }
    }
}
