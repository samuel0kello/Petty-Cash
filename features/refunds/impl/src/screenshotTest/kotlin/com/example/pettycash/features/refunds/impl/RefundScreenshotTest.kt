package com.example.pettycash.features.refunds.impl

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
import com.example.pettycash.features.refunds.impl.data.RefundContext
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundScreen
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundState
import com.example.pettycash.features.refunds.impl.ui.claim.RefundStep
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

private val context = RefundContext(
    categories = listOf("Transport", "Meals", "Supplies"),
    memberName = "Brian Otieno",
    memberPhone = "+254 712 345 678",
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Refund amount")
@Composable
fun RefundAmountScreenshot() {
    MaiTheme {
        PreviewDevice {
            ClaimRefundScreen(
                state = ClaimRefundState(amountDigits = "850", context = context),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Refund details")
@Composable
fun RefundDetailsScreenshot() {
    MaiTheme {
        PreviewDevice {
            ClaimRefundScreen(
                state = ClaimRefundState(
                    step = RefundStep.Details,
                    amountDigits = "850",
                    context = context,
                    purpose = "Matatu fare to Garissa site office",
                    category = "Transport",
                    date = "26 Oct 2026",
                    note = "Site visit was approved by Grace.",
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Refund review")
@Composable
fun RefundReviewScreenshot() {
    MaiTheme {
        PreviewDevice {
            ClaimRefundScreen(
                state = ClaimRefundState(
                    step = RefundStep.Review,
                    amountDigits = "850",
                    context = context,
                    purpose = "Matatu fare to Garissa site office",
                    category = "Transport",
                    date = "26 Oct 2026",
                    refundTo = "0712 ••• 678",
                ),
                onAction = {},
            )
        }
    }
}
