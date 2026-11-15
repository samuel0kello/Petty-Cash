package com.example.pettycash.features.floats.impl.ui.floats

import com.example.pettycash.features.floats.impl.data.FloatSnapshot
import com.example.pettycash.features.floats.impl.data.FloatState

data class FloatsState(
    val loading: Boolean = true,
    val snapshot: FloatSnapshot? = null,
)

sealed interface FloatsAction {
    data class OpenFloat(val state: FloatState) : FloatsAction
}

sealed interface FloatsEvent {
    data class OpenFloatDetail(val state: FloatState) : FloatsEvent
}

data class FloatDetailState(
    val loading: Boolean = true,
    val snapshot: FloatSnapshot? = null,
    val monthLabel: String = "",
)

sealed interface FloatDetailAction {
    data object TopUp : FloatDetailAction
    data object StartReconciliation : FloatDetailAction
}

sealed interface FloatDetailEvent {
    data object TopUp : FloatDetailEvent
    data object StartReconciliation : FloatDetailEvent
}

data class TopUpState(
    val amountDigits: String = "",
    val saving: Boolean = false,
    val doneAmountKes: Long? = null,
    val error: String? = null,
) {
    val amountKes: Long get() = amountDigits.toLongOrNull() ?: 0L
    val canContinue: Boolean get() = amountKes > 0 && !saving
}

sealed interface TopUpAction {
    data class Digit(val key: String) : TopUpAction
    data object Backspace : TopUpAction
    data object Next : TopUpAction
    data object Done : TopUpAction
}

sealed interface TopUpEvent {
    data class Saved(val amountKes: Long) : TopUpEvent
    data object Back : TopUpEvent
}
