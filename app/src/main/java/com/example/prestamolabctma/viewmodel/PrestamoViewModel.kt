package com.example.prestamolabctma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prestamolabctma.data.InMemoryPrestamoRepository
import com.example.prestamolabctma.data.PrestamoRepository
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo
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
    private val repository: PrestamoRepository =
        InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(

            PrestamoUiState(

                equipos =
                    repository.listarEquipos(),

                solicitudes =
                    repository.listarSolicitudes()
            )
        )

    val uiState: StateFlow<PrestamoUiState> =
        _uiState.asStateFlow()

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

        _uiState.value =
            _uiState.value.copy(

                equipos =
                    repository.listarEquipos(),

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

        _uiState.value =
            _uiState.value.copy(

                equipos =
                    repository.listarEquipos(),

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