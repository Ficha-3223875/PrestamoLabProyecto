package com.example.prestamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prestamolabctma.data.InMemoryPrestamoRepository
import com.example.prestamolabctma.data.PrestamoRepository
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.lifecycle.viewModelScope
import com.example.prestamolabctma.data.RoomPrestamoRepository
import com.example.prestamolabctma.data.local.PreferenciaDataStore
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false,
    val filtroCategoria: String = "TODOS"
)

class PrestamoViewModel(
    private val repository: PrestamoRepository = InMemoryPrestamoRepository(),
    private val dataStore: PreferenciaDataStore? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        // Si el repositorio es de Room (RoomPrestamoRepository) y hay DataStore, recolectamos reactivamente
        if (repository is RoomPrestamoRepository && dataStore != null) {
            viewModelScope.launch {
                combine(
                    repository.equiposFlow,
                    repository.solicitudesFlow,
                    dataStore.categoriaFiltroFlow
                ) { listaEquipos, listaSolicitudes, filtro ->
                    val equiposFiltrados = if (filtro == "TODOS") {
                        listaEquipos
                    } else {
                        listaEquipos.filter { it.categoria.name == filtro }
                    }
                    _uiState.value.copy(
                        equipos = equiposFiltrados,
                        solicitudes = listaSolicitudes,
                        filtroCategoria = filtro
                    )
                }.collect { nuevoEstado ->
                    _uiState.value = nuevoEstado
                }
            }
        } else {
            // Respaldar comportamiento inicial para la memoria y pruebas unitarias anteriores
            _uiState.value = PrestamoUiState(
                equipos = repository.listarEquipos(),
                solicitudes = repository.listarSolicitudes()
            )
        }
    }

    fun cambiarFiltroCategoria(categoria: String) {
        if (dataStore != null) {
            viewModelScope.launch {
                dataStore.guardarCategoriaFiltro(categoria)
            }
        } else {
            val todosEquipos = repository.listarEquipos()
            val filtrados = if (categoria == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == categoria }
            _uiState.value = _uiState.value.copy(
                equipos = filtrados,
                filtroCategoria = categoria
            )
        }
    }

    fun equipo(id: Int): Equipo? {
        return repository.obtenerEquipo(id)
    }

    fun solicitud(id: Int): SolicitudPrestamo? {
        return repository.obtenerSolicitud(id)
    }

    fun crearSolicitud(

        equipoId: Int,

        ambienteDestino: String,

        proposito: String,

        duracionHoras: Int

    ): Boolean {

        if (_uiState.value.guardando) {
            return false
        }

        _uiState.value =
            _uiState.value.copy(
                guardando = true,
                mensaje = null
            )

        val resultado =
            repository.crearSolicitud(

                equipoId,

                ambienteDestino,

                proposito,

                duracionHoras
            )

        val todosEquipos = repository.listarEquipos()
        val filtroActual = _uiState.value.filtroCategoria
        val equiposFiltrados = if (filtroActual == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == filtroActual }

        _uiState.value =
            _uiState.value.copy(

                equipos = equiposFiltrados,

                solicitudes =
                    repository.listarSolicitudes(),

                mensaje =
                    resultado.fold(

                        onSuccess = {
                            "Solicitud creada correctamente."
                        },

                        onFailure = {
                            it.message
                                ?: "No se pudo crear la solicitud."
                        }
                    ),

                guardando = false
            )

        return resultado.isSuccess
    }

    fun cancelarSolicitud(
        id: Int
    ): Boolean {

        val resultado =
            repository.cancelarSolicitud(id)

        val todosEquipos = repository.listarEquipos()
        val filtroActual = _uiState.value.filtroCategoria
        val equiposFiltrados = if (filtroActual == "TODOS") todosEquipos else todosEquipos.filter { it.categoria.name == filtroActual }

        _uiState.value =
            _uiState.value.copy(

                equipos = equiposFiltrados,

                solicitudes =
                    repository.listarSolicitudes(),

                mensaje =
                    resultado.fold(

                        onSuccess = {
                            "Solicitud cancelada."
                        },

                        onFailure = {
                            it.message
                                ?: "No se pudo cancelar."
                        }
                    )
            )

        return resultado.isSuccess
    }

    fun limpiarMensaje() {

        _uiState.value =
            _uiState.value.copy(
                mensaje = null
            )
    }
}