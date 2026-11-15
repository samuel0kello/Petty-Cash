package com.example.pettycash.features.records.impl

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
import com.example.pettycash.features.records.impl.data.ActivityItem
import com.example.pettycash.features.records.impl.data.ActivityKind
import com.example.pettycash.features.records.impl.ui.records.ActivityFilter
import com.example.pettycash.features.records.impl.ui.records.ActivityScreen
import com.example.pettycash.features.records.impl.ui.records.ActivityState
import com.example.pettycash.features.records.impl.ui.records.MemberRequestsScreen
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

private fun sampleItems() = listOf(
    ActivityItem(
        id = "r1", kind = ActivityKind.Request, kindId = "req-1",
        title = "Fuel for Garissa site visit", subtitle = "Cash in advance",
        amountKes = 3_200, incoming = false, statusLabel = "Pending", status = "Pending",
        createdAt = 1_760_000_000_000,
    ),
    ActivityItem(
        id = "r2", kind = ActivityKind.Refund, kindId = "ref-1",
        title = "Matatu fare to Garissa site office", subtitle = "Refund claim",
        amountKes = 850, incoming = false, statusLabel = "Approved", status = "Approved",
        createdAt = 1_759_000_000_000,
    ),
    ActivityItem(
        id = "s1", kind = ActivityKind.Spend, kindId = "sp-1",
        title = "Printer paper and pens", subtitle = "Westlands HQ",
        amountKes = 1_500, incoming = false, statusLabel = "Paid", status = "Paid",
        createdAt = 1_758_000_000_000,
    ),
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Dark member requests")
@Composable
fun DarkMemberRequestsScreenshot() {
    MaiTheme(darkTheme = true) {
        PreviewDevice {
            MemberRequestsScreen(items = sampleItems(), onOpenDetail = { _, _ -> })
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Dark history")
@Composable
fun DarkHistoryScreenshot() {
    MaiTheme(darkTheme = true) {
        PreviewDevice {
            ActivityScreen(
                state = ActivityState(
                    loading = false,
                    items = sampleItems(),
                    filter = ActivityFilter.All,
                ),
                onAction = {},
            )
        }
    }
}
