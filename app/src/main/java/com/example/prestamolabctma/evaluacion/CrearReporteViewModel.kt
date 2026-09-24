package com.example.prestamolabctma.evaluacion

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CrearReporteViewModel(
    private val repository: ReporteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CrearUiState())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    fun actualizarTitulo(valor: String) {
        // 1. Limita la entrada a 80 caracteres
        val tituloLimitado = if (valor.length > 80) valor.substring(0, 80) else valor

        // 2. Retira el error si el texto ya es válido (longitud >= 4)
        val nuevoError = if (tituloLimitado.trim().length >= 4) {
            null
        } else {
            _uiState.value.errorTitulo
        }

        // 3. Actualiza una copia inmutable del estado
        _uiState.value = _uiState.value.copy(
            titulo = tituloLimitado,
            errorTitulo = nuevoError
        )
    }

    fun guardar() {
        val textoActual = _uiState.value.titulo

        // 1. Rechaza blanco o longitud menor de 4, publica un mensaje concreto y no invoca Repository
        if (textoActual.trim().isBlank() || textoActual.trim().length < 4) {
            _uiState.value = _uiState.value.copy(
                errorTitulo = "El título debe tener al menos 4 caracteres y no estar vacío."
            )
            return
        }

        // Evita envíos duplicados si ya está guardando
        if (_uiState.value.guardando) return

        _uiState.value = _uiState.value.copy(guardando = true)

        // 2. Con título válido, crea un Reporte con id no vacío, invoca Repository una sola vez y publica guardadoId
        val nuevoId = UUID.randomUUID().toString()
        val nuevoReporte = Reporte(
            id = nuevoId,
            titulo = textoActual.trim()
        )

        repository.agregar(nuevoReporte)

        _uiState.value = _uiState.value.copy(
            guardando = false,
            guardadoId = nuevoId
        )
    }
}
