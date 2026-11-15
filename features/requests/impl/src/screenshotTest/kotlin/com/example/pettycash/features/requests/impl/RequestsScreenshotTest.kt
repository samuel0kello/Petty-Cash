package com.example.pettycash.features.requests.impl

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
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestContext
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailScreen
import com.example.pettycash.features.requests.impl.ui.detail.RequestDetailState
import com.example.pettycash.features.requests.impl.ui.request.RequestCashScreen
import com.example.pettycash.features.requests.impl.ui.request.RequestCashState
import com.example.pettycash.features.requests.impl.ui.request.RequestStep
import com.example.pettycash.mai.theme.MaiTheme

@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

private val previewContext = RequestContext(
    perRequestLimitKes = 5_000L,
    projects = listOf("Garissa solar install"),
    sites = listOf("Garissa", "Nakuru"),
    categories = listOf("Fuel", "Transport", "Supplies", "Meals", "Other"),
    reviewerName = "Grace Wanjiku",
    memberName = "Brian Otieno",
)

private val previewRequest = CashRequest(
    id = "req-1",
    amountKes = 3_200,
    purpose = "Fuel for Garissa site visit",
    category = "Fuel",
    project = "Garissa solar install",
    site = "Garissa",
    neededBy = "Fri 30 Oct",
    requesterName = "Brian Otieno",
    reviewerName = "Grace Wanjiku",
    status = RequestStatus.PENDING,
    voucherCode = "PCV-1043",
    createdAt = 0L,
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Amount")
@Composable
fun AmountScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestCashScreen(
                state = RequestCashState(amountDigits = "3200", context = previewContext),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Amount Over Limit")
@Composable
fun AmountOverLimitScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestCashScreen(
                state = RequestCashState(amountDigits = "7500", context = previewContext),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Details")
@Composable
fun DetailsScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestCashScreen(
                state = RequestCashState(
                    step = RequestStep.Details,
                    amountDigits = "3200",
                    context = previewContext,
                    purpose = "Fuel for Garissa site visit",
                    category = "Fuel",
                    project = "Garissa solar install",
                    site = "Garissa",
                    neededBy = "Fri 30 Oct",
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Review")
@Composable
fun ReviewScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestCashScreen(
                state = RequestCashState(
                    step = RequestStep.Review,
                    amountDigits = "3200",
                    context = previewContext,
                    purpose = "Fuel for Garissa site visit",
                    category = "Fuel",
                    project = "Garissa solar install",
                    site = "Garissa",
                    neededBy = "Fri 30 Oct",
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Sent")
@Composable
fun SentScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestCashScreen(
                state = RequestCashState(step = RequestStep.Sent, sentRequest = previewRequest),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Detail Pending")
@Composable
fun DetailPendingScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestDetailScreen(
                state = RequestDetailState(request = previewRequest, loaded = true),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Detail Paid")
@Composable
fun DetailPaidScreenshot() {
    MaiTheme {
        PreviewDevice {
            RequestDetailScreen(
                state = RequestDetailState(
                    request = previewRequest.copy(
                        status = RequestStatus.PAID,
                        paidReference = "SGH7K2LMN4",
                    ),
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}
