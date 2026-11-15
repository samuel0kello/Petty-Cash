package com.example.pettycash.features.records.impl.ui.records

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.records.impl.data.RecordsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ActivityViewModel(
    private val repository: RecordsRepository,
) : MviViewModel<ActivityAction, ActivityEvent, ActivityState>(ActivityState()) {

    init {
        repository.items
            .onEach { items -> updateState { it.copy(loading = false, items = items) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ActivityAction) {
        when (action) {
            is ActivityAction.QueryChanged -> updateState { it.copy(query = action.value) }
            is ActivityAction.FilterSelected -> updateState { it.copy(filter = action.filter) }
            is ActivityAction.OpenItem -> sendEvent(ActivityEvent.OpenDetail(action.item.kind, action.item.kindId))
        }
    }
}
