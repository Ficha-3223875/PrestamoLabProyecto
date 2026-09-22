package com.example.prstamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)

class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _mensajeState = MutableStateFlow<String?>(null)
    private val _guardandoState = MutableStateFlow(false)

    // Combinamos los Flujos de Room con los estados de UI (mensajes y carga)
    val uiState: StateFlow<PrestamoUiState> = combine(
        repository.obtenerEquipos(),
        repository.obtenerSolicitudes(),
        _mensajeState,
        _guardandoState
    ) { equipos, solicitudes, mensaje, guardando ->
        PrestamoUiState(
            equipos = equipos,
            solicitudes = solicitudes,
            mensaje = mensaje,
            guardando = guardando
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrestamoUiState()
    )

    fun obtenerEquipo(id: Int, onResult: (Equipo?) -> Unit) {
        viewModelScope.launch {
            val equipo = repository.obtenerEquipo(id)
            onResult(equipo)
        }
    }

    fun obtenerSolicitud(id: Int, onResult: (SolicitudPrestamo?) -> Unit) {
        viewModelScope.launch {
            val solicitud = repository.obtenerSolicitud(id)
            onResult(solicitud)
        }
    }

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ) {
        if (_guardandoState.value) return

        if (ambienteDestino.isBlank()) {
            mostrarMensaje("El ambiente o destino es obligatorio.")
            return
        }

        if (proposito.trim().length !in 10..180) {
            mostrarMensaje("El propósito debe tener entre 10 y 180 caracteres.")
            return
        }

        if (duracionHoras !in 1..8) {
            mostrarMensaje("La duración debe estar entre 1 y 8 horas.")
            return
        }

        _guardandoState.value = true
        _mensajeState.value = null

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = com.example.prstamolabctma.model.EstadoSolicitud.SOLICITADA
        )

        viewModelScope.launch {
            val resultado = repository.crearSolicitud(solicitud)
            resultado
                .onSuccess {
                    _mensajeState.value = "Solicitud creada correctamente."
                    _guardandoState.value = false
                }
                .onFailure { error ->
                    _mensajeState.value = error.message ?: "No se pudo crear la solicitud."
                    _guardandoState.value = false
                }
        }
    }

    fun cancelarSolicitud(id: Int) {
        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(id)
            resultado
                .onSuccess {
                    _mensajeState.value = "Solicitud cancelada correctamente."
                }
                .onFailure { error ->
                    mostrarMensaje(error.message ?: "No se pudo cancelar la solicitud.")
                }
        }
    }

    fun limpiarMensaje() {
        _mensajeState.value = null
    }

    private fun mostrarMensaje(mensaje: String) {
        _mensajeState.value = mensaje
    }
}