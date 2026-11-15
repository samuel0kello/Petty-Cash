package com.example.pettycash.features.spends.impl.ui.record

import com.example.pettycash.features.spends.impl.data.SpendContext
import com.example.pettycash.features.spends.impl.data.SpendItem

enum class SpendStep { Amount, Details, PaidWith, Receipt, Review }
enum class ReceiptStage { Capture, Preview, NoReceipt }
enum class PayMethod { Mpesa, Cash }

data class RecordSpendState(
    val step: SpendStep = SpendStep.Amount,
    val amountDigits: String = "",
    val context: SpendContext? = null,
    val items: List<SpendItem> = listOf(SpendItem()),
    val category: String = "",
    val project: String = "",
    val site: String = "",
    val paidTo: String = "",
    val method: PayMethod = PayMethod.Mpesa,
    val mpesaCode: String = "",
    val receiptStage: ReceiptStage = ReceiptStage.Capture,
    val hasReceipt: Boolean = false,
    val saving: Boolean = false,
)

sealed interface RecordSpendAction {
    data class AmountKey(val key: String) : RecordSpendAction
    data object AmountBackspace : RecordSpendAction
    data class ItemDescription(val index: Int, val value: String) : RecordSpendAction
    data class ItemAmount(val index: Int, val digits: String) : RecordSpendAction
    data object AddItem : RecordSpendAction
    data class RemoveItem(val index: Int) : RecordSpendAction
    data class CategoryChanged(val value: String) : RecordSpendAction
    data class ProjectChanged(val value: String) : RecordSpendAction
    data class SiteChanged(val value: String) : RecordSpendAction
    data class PaidToChanged(val value: String) : RecordSpendAction
    data class MethodSelected(val method: PayMethod) : RecordSpendAction
    data class MpesaCodeChanged(val value: String) : RecordSpendAction
    data object Shutter : RecordSpendAction
    data object UsePhoto : RecordSpendAction
    data object Retake : RecordSpendAction
    data object NoReceipt : RecordSpendAction
    data object BackToCapture : RecordSpendAction
    data object ContinueWithoutReceipt : RecordSpendAction
    data object Next : RecordSpendAction
    data object Back : RecordSpendAction
    data object Save : RecordSpendAction
}

sealed interface RecordSpendEvent {
    data object NavigateHome : RecordSpendEvent
}
