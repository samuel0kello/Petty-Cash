package com.example.pettycash.features.spends.impl.ui.record

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.spends.impl.data.SpendItem
import com.example.pettycash.features.spends.impl.data.SpendsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RecordSpendViewModel(
    private val repository: SpendsRepository,
) : MviViewModel<RecordSpendAction, RecordSpendEvent, RecordSpendState>(RecordSpendState()) {

    init {
        repository.snapshot
            .onEach { (spends, context) ->
                updateState { it.copy(context = context) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: RecordSpendAction) {
        when (action) {
            is RecordSpendAction.AmountKey -> updateState {
                it.copy(amountDigits = (it.amountDigits + action.key).take(9))
            }
            RecordSpendAction.AmountBackspace -> updateState {
                it.copy(amountDigits = it.amountDigits.dropLast(1))
            }
            is RecordSpendAction.ItemDescription -> updateItem(action.index) { it.copy(description = action.value) }
            is RecordSpendAction.ItemAmount -> updateItem(action.index) { item ->
                item.copy(amountKes = action.digits.filter { c -> c.isDigit() }.toLongOrNull() ?: 0)
            }
            RecordSpendAction.AddItem -> updateState {
                it.copy(items = it.items + SpendItem())
            }
            is RecordSpendAction.RemoveItem -> updateState {
                it.copy(items = it.items.filterIndexed { i, _ -> i != action.index })
            }
            is RecordSpendAction.CategoryChanged -> updateState { it.copy(category = action.value) }
            is RecordSpendAction.ProjectChanged -> updateState { it.copy(project = action.value) }
            is RecordSpendAction.SiteChanged -> updateState { it.copy(site = action.value) }
            is RecordSpendAction.PaidToChanged -> updateState { it.copy(paidTo = action.value) }
            is RecordSpendAction.MethodSelected -> updateState {
                it.copy(method = action.method, mpesaCode = if (action.method == PayMethod.Cash) "" else it.mpesaCode)
            }
            is RecordSpendAction.MpesaCodeChanged -> updateState {
                it.copy(mpesaCode = action.value.filter { c -> c.isLetterOrDigit() }.take(12).uppercase())
            }
            RecordSpendAction.Shutter -> updateState {
                it.copy(receiptStage = ReceiptStage.Preview)
            }
            RecordSpendAction.UsePhoto -> updateState {
                it.copy(hasReceipt = true, step = SpendStep.Review)
            }
            RecordSpendAction.Retake -> updateState { it.copy(receiptStage = ReceiptStage.Capture) }
            RecordSpendAction.NoReceipt -> updateState { it.copy(receiptStage = ReceiptStage.NoReceipt) }
            RecordSpendAction.BackToCapture -> updateState { it.copy(receiptStage = ReceiptStage.Capture) }
            RecordSpendAction.ContinueWithoutReceipt -> updateState {
                it.copy(hasReceipt = false, step = SpendStep.Review)
            }
            RecordSpendAction.Next -> onNext()
            RecordSpendAction.Back -> onBack()
            RecordSpendAction.Save -> onSave()
        }
    }

    private fun updateItem(index: Int, transform: (SpendItem) -> SpendItem) {
        updateState { state ->
            state.copy(items = state.items.mapIndexed { i, item -> if (i == index) transform(item) else item })
        }
    }

    private fun onNext() {
        val state = currentState
        when (state.step) {
            SpendStep.Amount -> {
                val float = state.context?.floatKes
                if (amount(state) > 0 && (float == null || amount(state) <= float)) {
                    updateState { it.copy(step = SpendStep.Details) }
                }
            }
            SpendStep.Details -> if (itemsTotal(state) > 0) updateState { it.copy(step = SpendStep.PaidWith) }
            SpendStep.PaidWith -> {
                if (state.method == PayMethod.Cash || state.mpesaCode.length >= 6) {
                    updateState { it.copy(step = SpendStep.Receipt, receiptStage = ReceiptStage.Capture) }
                }
            }
            SpendStep.Receipt -> Unit
            SpendStep.Review -> Unit
        }
    }

    private fun onBack() {
        val state = currentState
        when (state.step) {
            SpendStep.Details -> updateState { it.copy(step = SpendStep.Amount) }
            SpendStep.PaidWith -> updateState { it.copy(step = SpendStep.Details) }
            SpendStep.Receipt -> updateState { it.copy(step = SpendStep.PaidWith, receiptStage = ReceiptStage.Capture) }
            SpendStep.Review -> updateState {
                it.copy(
                    step = if (it.hasReceipt || it.receiptStage == ReceiptStage.NoReceipt) SpendStep.Receipt else SpendStep.PaidWith,
                    receiptStage = ReceiptStage.Capture,
                )
            }
            SpendStep.Amount -> sendEvent(RecordSpendEvent.NavigateHome)
        }
    }

    private fun onSave() {
        val state = currentState
        if (state.saving) return
        updateState { it.copy(saving = true) }
        viewModelScope.launch {
            repository.saveSpend(
                amountKes = amount(state),
                items = state.items.filter { it.description.isNotBlank() || it.amountKes > 0 },
                category = state.category,
                project = state.project,
                site = state.site,
                paidTo = state.paidTo,
                method = if (state.method == PayMethod.Mpesa) "M-Pesa" else "Cash",
                mpesaCode = state.mpesaCode,
                hasReceipt = state.hasReceipt,
            )
            sendEvent(RecordSpendEvent.NavigateHome)
        }
    }
}

fun amount(state: RecordSpendState): Long = state.amountDigits.toLongOrNull() ?: 0L

fun itemsTotal(state: RecordSpendState): Long = state.items.sumOf { it.amountKes }
