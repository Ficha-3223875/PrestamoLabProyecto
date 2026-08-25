package com.example.prstamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prstamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())

    val uiState: StateFlow<PrestamoUiState> =
        _uiState.asStateFlow()

    init {
        actualizarDatos()
    }

    private fun actualizarDatos() {
        _uiState.value = _uiState.value.copy(
            equipos = repository.obtenerEquipos(),
            solicitudes = repository.obtenerSolicitudes()
        )
    }

    fun obtenerEquipo(id: Int): Equipo? {
        return repository.obtenerEquipo(id)
    }

    fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return repository.obtenerSolicitud(id)
    }

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ) {

        if (_uiState.value.guardando) {
            return
        }

        if (ambienteDestino.isBlank()) {
            mostrarMensaje("El ambiente o destino es obligatorio.")
            return
        }

        if (proposito.trim().length !in 10..180) {
            mostrarMensaje(
                "El propósito debe tener entre 10 y 180 caracteres."
            )
            return
        }

        if (duracionHoras !in 1..8) {
            mostrarMensaje(
                "La duración debe estar entre 1 y 8 horas."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            guardando = true,
            mensaje = null
        )

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = com.example.prstamolabctma.model.EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(solicitud)

        resultado
            .onSuccess {
                actualizarDatos()

                _uiState.value = _uiState.value.copy(
                    mensaje = "Solicitud creada correctamente.",
                    guardando = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    mensaje = error.message
                        ?: "No se pudo crear la solicitud.",
                    guardando = false
                )
            }
    }

    fun cancelarSolicitud(id: Int) {

        val resultado = repository.cancelarSolicitud(id)

        resultado
            .onSuccess {
                actualizarDatos()

                _uiState.value = _uiState.value.copy(
                    mensaje = "Solicitud cancelada correctamente."
                )
            }
            .onFailure { error ->
                mostrarMensaje(
                    error.message ?: "No se pudo cancelar la solicitud."
                )
            }
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(
            mensaje = null
        )
    }

    private fun mostrarMensaje(mensaje: String) {
        _uiState.value = _uiState.value.copy(
            mensaje = mensaje
        )
    }
}