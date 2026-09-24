package com.example.prstamolabctma.ui.state

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Contenido<T>(val datos: T) : UiState<T>
    data object Vacio : UiState<Nothing>
    data class Error(val mensaje: String) : UiState<Nothing>
}
