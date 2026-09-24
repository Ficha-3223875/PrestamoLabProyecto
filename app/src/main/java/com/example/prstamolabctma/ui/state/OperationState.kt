package com.example.prstamolabctma.ui.state

sealed interface OperationState {
    data object Inactiva : OperationState
    data object EnCurso : OperationState
    data class Exitosa(val mensaje: String) : OperationState
    data class Fallida(val mensaje: String) : OperationState
}
