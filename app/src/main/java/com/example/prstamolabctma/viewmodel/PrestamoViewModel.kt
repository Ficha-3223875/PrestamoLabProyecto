package com.example.prstamolabctma.viewmodel
import com.example.prstamolabctma.model.EstadoEquipo


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrestamoViewModel(private val repository: PrestamoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState

    fun cargarEquipos() {
        _uiState.value = _uiState.value.copy(equipos = repository.obtenerEquipos())
    }

    fun crearSolicitud(equipoId: Int, destino: String, proposito: String, horas: Int) {
        val solicitudesActualizadas = _uiState.value.solicitudes.toMutableList()
        val nuevaSolicitud = SolicitudPrestamo(
            id = solicitudesActualizadas.size + 1,
            equipoId = equipoId,
            ambienteDestino = destino,
            proposito = proposito,   // ✅ ahora sí se guarda
            duracionHoras = horas,
            estado = EstadoSolicitud.SOLICITADA
        )
        solicitudesActualizadas.add(nuevaSolicitud)

        val equiposActualizados = _uiState.value.equipos.map { equipo ->
            if (equipo.id == equipoId) {
                equipo.copy(estado = EstadoEquipo.RESERVADO)
            } else equipo
        }

        _uiState.value = _uiState.value.copy(
            solicitudes = solicitudesActualizadas,
            equipos = equiposActualizados
        )
    }



    fun cancelarSolicitud(id: Int) {
        val solicitudesActualizadas = _uiState.value.solicitudes.map { solicitud ->
            if (solicitud.id == id) {
                solicitud.copy(estado = EstadoSolicitud.CANCELADA)
            } else solicitud
        }

        val solicitudCancelada = solicitudesActualizadas.find { it.id == id }
        val equiposActualizados = _uiState.value.equipos.map { equipo ->
            if (equipo.id == solicitudCancelada?.equipoId) {
                equipo.copy(estado = EstadoEquipo.DISPONIBLE)
            } else equipo
        }

        _uiState.value = _uiState.value.copy(
            solicitudes = solicitudesActualizadas,
            equipos = equiposActualizados
        )
    }

}

