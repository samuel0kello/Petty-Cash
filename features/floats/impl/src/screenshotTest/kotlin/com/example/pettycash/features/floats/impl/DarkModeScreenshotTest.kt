package com.example.pettycash.features.floats.impl

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
import com.example.pettycash.features.floats.impl.data.FloatSnapshot
import com.example.pettycash.features.floats.impl.data.FloatState
import com.example.pettycash.features.floats.impl.data.SpendRow
import com.example.pettycash.features.floats.impl.ui.floats.FloatsScreen
import com.example.pettycash.features.floats.impl.ui.floats.FloatsState
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

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Dark floats")
@Composable
fun DarkFloatsScreenshot() {
    MaiTheme(darkTheme = true) {
        PreviewDevice {
            FloatsScreen(
                state = FloatsState(
                    loading = false,
                    snapshot = FloatSnapshot(
                        state = FloatState(
                            floatName = "Westlands office float",
                            custodianName = "Wanjiru Kamau",
                            balanceKes = 18_450,
                            issuedKes = 25_000,
                            spentKes = 6_550,
                        ),
                        spends = listOf(
                            SpendRow("s1", 1_500, "Westlands HQ", "Supplies", true, 1_758_000_000_000),
                            SpendRow("s2", 4_500, "Garissa", "Fuel", true, 1_757_000_000_000),
                        ),
                    ),
                ),
                onAction = {},
            )
        }
    }
}
