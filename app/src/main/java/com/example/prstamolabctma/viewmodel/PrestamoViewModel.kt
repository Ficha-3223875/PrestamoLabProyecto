package com.example.miformacionctma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miformacionctma.model.EstadoSolicitud
import com.example.miformacionctma.model.SolicitudPrestamo
import com.example.miformacionctma.repository.InMemoryPrestamoRepository
import com.example.miformacionctma.repository.PrestamoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrestamoUiState(
    val cargando: Boolean = false,
    val mensaje: String? = null
)

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    fun obtenerEquipos() = repository.obtenerEquipos()

    fun obtenerEquipo(id: Int) = repository.obtenerEquipo(id)

    fun obtenerSolicitudes() = repository.obtenerSolicitudes()

    fun obtenerSolicitud(id: Int) = repository.obtenerSolicitud(id)

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Boolean {

        if (ambienteDestino.isBlank()) {
            mostrarMensaje("El ambiente de destino es obligatorio")
            return false
        }

        if (proposito.length !in 10..180) {
            mostrarMensaje(
                "El propósito debe tener entre 10 y 180 caracteres"
            )
            return false
        }

        if (duracionHoras !in 1..8) {
            mostrarMensaje(
                "La duración debe estar entre 1 y 8 horas"
            )
            return false
        }

        val equipo = repository.obtenerEquipo(equipoId)

        if (equipo == null) {
            mostrarMensaje("El equipo no existe")
            return false
        }

        if (equipo.estado.name != "DISPONIBLE") {
            mostrarMensaje("El equipo no está disponible")
            return false
        }

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(solicitud)

        return if (resultado.isSuccess) {
            mostrarMensaje("Solicitud creada correctamente")
            true
        } else {
            mostrarMensaje(
                resultado.exceptionOrNull()?.message
                    ?: "No se pudo crear la solicitud"
            )
            false
        }
    }

    fun cancelarSolicitud(id: Int): Boolean {

        val resultado = repository.cancelarSolicitud(id)

        return if (resultado.isSuccess) {
            mostrarMensaje("Solicitud cancelada correctamente")
            true
        } else {
            mostrarMensaje(
                resultado.exceptionOrNull()?.message
                    ?: "No se pudo cancelar la solicitud"
            )
            false
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