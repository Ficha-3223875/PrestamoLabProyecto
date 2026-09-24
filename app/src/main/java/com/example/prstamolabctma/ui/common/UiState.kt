package com.example.prstamolabctma.ui.common

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Content<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>
}
