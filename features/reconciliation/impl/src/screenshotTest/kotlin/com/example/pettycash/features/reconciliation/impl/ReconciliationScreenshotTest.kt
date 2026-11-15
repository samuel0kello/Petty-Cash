package com.example.pettycash.features.reconciliation.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationContext
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationRecord
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationSnapshot
import com.example.pettycash.features.reconciliation.impl.data.UnreceiptedSpend
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationScreen
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationState
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconStep
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

private val context = ReconciliationContext(
    expectedKes = 18_450,
    floatName = "Westlands office float",
    unreceiptedSpends = listOf(
        UnreceiptedSpend("u1", "Cash withdrawal, Westlands HQ", 800),
        UnreceiptedSpend("u2", "Cash withdrawal, Westlands HQ", 700),
    ),
)

private val record = ReconciliationRecord(
    id = "rec-1",
    month = "September",
    countedKes = 2_450,
    expectedKes = 18_450,
    varianceKes = -15_550,
    explanation = "Matatu fares and tea. No receipts.",
    status = "Submitted",
    createdAt = 1_759_000_000_000,
    unreceiptedCount = 2,
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Recon count")
@Composable
fun ReconCountScreenshot() {
    MaiTheme {
        PreviewDevice {
            ReconciliationScreen(
                state = ReconciliationState(
                    step = ReconStep.Count,
                    amountDigits = "2450",
                    snapshot = ReconciliationSnapshot(record = null, context = context),
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Recon result")
@Composable
fun ReconResultScreenshot() {
    MaiTheme {
        PreviewDevice {
            ReconciliationScreen(
                state = ReconciliationState(
                    step = ReconStep.Result,
                    amountDigits = "2450",
                    snapshot = ReconciliationSnapshot(record = record, context = context),
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Recon admin review")
@Composable
fun ReconAdminReviewScreenshot() {
    MaiTheme {
        PreviewDevice {
            ReconciliationScreen(
                state = ReconciliationState(
                    step = ReconStep.AdminReview,
                    snapshot = ReconciliationSnapshot(record = record, context = context, isAdmin = true),
                ),
                onAction = {},
            )
        }
    }
}
