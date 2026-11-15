package com.example.pettycash.presentation

sealed interface ViewState<out T> {
    data object Loading : ViewState<Nothing>
    data class Success<T>(val data: T) : ViewState<T>
    data class Error(val message: String?) : ViewState<Nothing>
}

sealed interface ViewListState<out T> {
    data object Loading : ViewListState<Nothing>
    data object Empty : ViewListState<Nothing>
    data class NotEmpty<T>(val items: List<T>) : ViewListState<T>
    data class Error(val message: String?) : ViewListState<Nothing>
}
