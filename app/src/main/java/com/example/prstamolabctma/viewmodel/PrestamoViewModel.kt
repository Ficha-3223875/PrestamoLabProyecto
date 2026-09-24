package com.example.prstamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.ui.common.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _mensajeState = MutableStateFlow<String?>(null)
    private val _guardandoState = MutableStateFlow(false)

    // Manejador global de excepciones para corrutinas en ViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _mensajeState.value = throwable.message ?: "Ocurrió un error inesperado."
        _guardandoState.value = false
    }

    // StateFlow reactivo de Equipos mapeado a UiState (Loading, Content, Empty, Error)
    val equiposState: StateFlow<UiState<List<Equipo>>> = repository.obtenerEquipos()
        .map<List<Equipo>, UiState<List<Equipo>>> { equipos ->
            if (equipos.isEmpty()) UiState.Empty else UiState.Content(equipos)
        }
        .catch { e ->
            emit(UiState.Error(e.message ?: "Error al obtener la lista de equipos.", e))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    // StateFlow reactivo de Solicitudes mapeado a UiState (Loading, Content, Empty, Error)
    val solicitudesState: StateFlow<UiState<List<SolicitudPrestamo>>> = repository.obtenerSolicitudes()
        .map<List<SolicitudPrestamo>, UiState<List<SolicitudPrestamo>>> { solicitudes ->
            if (solicitudes.isEmpty()) UiState.Empty else UiState.Content(solicitudes)
        }
        .catch { e ->
            emit(UiState.Error(e.message ?: "Error al obtener las solicitudes.", e))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    // Estado combinado para flujos generales y compatibilidad
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
    }
        .catch { e ->
            _mensajeState.value = e.message
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PrestamoUiState()
        )

    fun obtenerEquipoState(id: Int): StateFlow<UiState<Equipo>> {
        return repository.obtenerEquipo(id)
            .map<Equipo?, UiState<Equipo>> { equipo ->
                if (equipo == null) UiState.Empty else UiState.Content(equipo)
            }
            .catch { e ->
                emit(UiState.Error(e.message ?: "Error al obtener el equipo.", e))
            }
            .flowOn(ioDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = UiState.Loading
            )
    }

    fun obtenerSolicitudState(id: Int): StateFlow<UiState<SolicitudPrestamo>> {
        return repository.obtenerSolicitud(id)
            .map<SolicitudPrestamo?, UiState<SolicitudPrestamo>> { solicitud ->
                if (solicitud == null) UiState.Empty else UiState.Content(solicitud)
            }
            .catch { e ->
                emit(UiState.Error(e.message ?: "Error al obtener la solicitud.", e))
            }
            .flowOn(ioDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = UiState.Loading
            )
    }

    fun obtenerEquipo(id: Int, onResult: (Equipo?) -> Unit) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val equipo = repository.obtenerEquipo(id).firstOrNull()
            onResult(equipo)
        }
    }

    fun obtenerSolicitud(id: Int, onResult: (SolicitudPrestamo?) -> Unit) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val solicitud = repository.obtenerSolicitud(id).firstOrNull()
            onResult(solicitud)
        }
    }

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int,
        evidenciaUri: String? = null,
        latitud: Double? = null,
        longitud: Double? = null
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
            estado = EstadoSolicitud.SOLICITADA,
            evidenciaUri = evidenciaUri,
            latitud = latitud,
            longitud = longitud
        )

        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            try {
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
            } catch (e: Exception) {
                _mensajeState.value = e.message ?: "No se pudo crear la solicitud."
                _guardandoState.value = false
            }
        }
    }

    fun cancelarSolicitud(id: Int) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            try {
                val resultado = repository.cancelarSolicitud(id)
                resultado
                    .onSuccess {
                        _mensajeState.value = "Solicitud cancelada correctamente."
                    }
                    .onFailure { error ->
                        mostrarMensaje(error.message ?: "No se pudo cancelar la solicitud.")
                    }
            } catch (e: Exception) {
                mostrarMensaje(e.message ?: "No se pudo cancelar la solicitud.")
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