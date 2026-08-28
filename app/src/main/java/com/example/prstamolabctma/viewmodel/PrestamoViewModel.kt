package com.example.prstamolabctma.viewmodel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.prstamolabctma.model.*
import com.example.prstamolabctma.repository.InMemoryPrestamoRepository

class PrestamoViewModel : ViewModel() {
    private val repo = InMemoryPrestamoRepository()

    private val _equipos = MutableStateFlow(repo.obtenerEquipos())
    val equipos: StateFlow<List<Equipo>> = _equipos

    private val _solicitudes = MutableStateFlow(repo.obtenerSolicitudes())
    val solicitudes: StateFlow<List<SolicitudPrestamo>> = _solicitudes

    fun crearSolicitud(solicitud: SolicitudPrestamo) {
        val result = repo.crearSolicitud(solicitud)
        if (result.isSuccess) {
            _equipos.value = repo.obtenerEquipos()
            _solicitudes.value = repo.obtenerSolicitudes()
        }
    }

    fun cancelarSolicitud(id: Int) {
        val result = repo.cancelarSolicitud(id)
        if (result.isSuccess) {
            _equipos.value = repo.obtenerEquipos()
            _solicitudes.value = repo.obtenerSolicitudes()
        }
    }
}
