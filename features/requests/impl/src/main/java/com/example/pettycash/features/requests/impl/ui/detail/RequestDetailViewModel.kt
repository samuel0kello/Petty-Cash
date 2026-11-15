package com.example.pettycash.features.requests.impl.ui.detail

import androidx.lifecycle.viewModelScope
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.presentation.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RequestDetailViewModel(
    private val repository: RequestsRepository,
    private val requestId: String,
) : MviViewModel<RequestDetailAction, RequestDetailEvent, RequestDetailState>(RequestDetailState()) {

    init {
        repository.snapshot
            .onEach { (requests, _) ->
                val request = requests.firstOrNull { it.id == requestId }
                updateState { it.copy(request = request, loaded = true) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: RequestDetailAction) {
        when (action) {
            RequestDetailAction.Back -> sendEvent(RequestDetailEvent.NavigateBack)
            RequestDetailAction.Done -> sendEvent(RequestDetailEvent.NavigateHome)
            RequestDetailAction.AddReceipt,
            RequestDetailAction.Withdraw,
            -> Unit
        }
    }
}
