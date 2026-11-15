package com.example.pettycash.mai

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.MaiTransactionRow
import com.example.pettycash.mai.components.MaiRowDirection
import com.example.pettycash.mai.theme.MaiTheme

@PreviewTest
@Preview(showBackground = true, name = "Status chips")
@Composable
fun StatusChipsPreview() {
    MaiTheme {
        androidx.compose.foundation.layout.Column {
            MaiStatus.entries.forEach { MaiStatusChip(it) }
        }
    }
}

@PreviewTest
@Preview(showBackground = true, name = "Buttons")
@Composable
fun ButtonsPreview() {
    MaiTheme {
        androidx.compose.foundation.layout.Column {
            MaiButtonType.entries.forEach { MaiButton("Button", onClick = {}, type = it) }
        }
    }
}

@PreviewTest
@Preview(showBackground = true, name = "Transaction row")
@Composable
fun TransactionRowPreview() {
    MaiTheme {
        MaiTransactionRow(
            title = "Fuel for generator",
            meta = "Kilimani site · 28 Oct",
            amountKes = 4500,
            direction = MaiRowDirection.Out,
            status = MaiStatus.Approved,
        )
    }
}
