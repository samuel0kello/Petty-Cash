package com.example.pettycash.features.reconciliation.impl.ui.reconciliation

import com.example.pettycash.features.reconciliation.impl.data.ReconciliationSnapshot

enum class ReconStep { Count, Helper, Result, Explain, Submitted, AdminReview, MonthClosed }

data class ReconciliationState(
    val step: ReconStep = ReconStep.Count,
    val amountDigits: String = "",
    val snapshot: ReconciliationSnapshot? = null,
    val explanation: String = "",
    val denominationKes: Map<Int, String> = emptyMap(),
    val submitting: Boolean = false,
    val error: String? = null,
) {
    val countedKes: Long get() = amountDigits.toLongOrNull() ?: 0L
    val helperTotal: Long get() = denominationKes.entries.sumOf { (kes, count) ->
        (count.filter(Char::isDigit).toLongOrNull() ?: 0L) * kes
    }
}

sealed interface ReconciliationAction {
    data class Digit(val key: String) : ReconciliationAction
    data object Backspace : ReconciliationAction
    data object Next : ReconciliationAction
    data object UseHelperTotal : ReconciliationAction
    data object ToggleHelper : ReconciliationAction
    data class Denomination(val kes: Int, val value: String) : ReconciliationAction
    data class ExplanationChanged(val value: String) : ReconciliationAction
    data object Explain : ReconciliationAction
    data object Submit : ReconciliationAction
    data object CloseMonth : ReconciliationAction
    data object Back : ReconciliationAction
    data object Done : ReconciliationAction
}

sealed interface ReconciliationEvent {
    data object Back : ReconciliationEvent
    data object Done : ReconciliationEvent
}
