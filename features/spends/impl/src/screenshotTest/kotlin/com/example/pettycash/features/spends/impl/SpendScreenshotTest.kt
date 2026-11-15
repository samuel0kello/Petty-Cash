package com.example.pettycash.features.spends.impl

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
import com.example.pettycash.features.spends.impl.data.SpendContext
import com.example.pettycash.features.spends.impl.data.SpendItem
import com.example.pettycash.features.spends.impl.ui.record.PayMethod
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendScreen
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendState
import com.example.pettycash.features.spends.impl.ui.record.SpendStep
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

private val context = SpendContext(
    categories = listOf("Fuel", "Supplies", "Transport"),
    projects = listOf("Garissa solar install"),
    sites = listOf("Garissa", "Westlands HQ"),
    memberName = "Wanjiru Kamau",
    floatKes = 18_450,
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Spend amount")
@Composable
fun SpendAmountScreenshot() {
    MaiTheme {
        PreviewDevice {
            RecordSpendScreen(
                state = RecordSpendState(amountDigits = "1500", context = context),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Spend details")
@Composable
fun SpendDetailsScreenshot() {
    MaiTheme {
        PreviewDevice {
            RecordSpendScreen(
                state = RecordSpendState(
                    step = SpendStep.Details,
                    amountDigits = "1500",
                    context = context,
                    items = listOf(SpendItem("Printer paper and pens", 1_500)),
                    category = "Supplies",
                    project = "Garissa solar install",
                    site = "Westlands HQ",
                    paidTo = "Stationers Ltd",
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Spend review")
@Composable
fun SpendReviewScreenshot() {
    MaiTheme {
        PreviewDevice {
            RecordSpendScreen(
                state = RecordSpendState(
                    step = SpendStep.Review,
                    amountDigits = "1500",
                    context = context,
                    items = listOf(SpendItem("Printer paper and pens", 1_500)),
                    category = "Supplies",
                    project = "Garissa solar install",
                    site = "Westlands HQ",
                    paidTo = "Stationers Ltd",
                    method = PayMethod.Mpesa,
                    mpesaCode = "QGH7X2KLMN",
                    hasReceipt = true,
                ),
                onAction = {},
            )
        }
    }
}
