package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryPrestamoRepository : PrestamoRepository {

    private val _equiposFlow = MutableStateFlow(
        listOf(
            Equipo(1, "Multímetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(2, "Cámara Canon", CategoriaEquipo.CAMARA, EstadoEquipo.DISPONIBLE),
            Equipo(3, "Tablet Samsung", CategoriaEquipo.TABLETA, EstadoEquipo.DISPONIBLE)
        )
    )

    private val _solicitudesFlow = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())

    // 👈 Implementación correcta como Flow (Semana 7)
    override fun obtenerEquipos(): Flow<List<Equipo>> = _equiposFlow.asStateFlow()

    override suspend fun obtenerEquipo(id: Int): Equipo? = _equiposFlow.value.find { it.id == id }

    // 👈 Implementación correcta como Flow (Semana 7)
    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> = _solicitudesFlow.asStateFlow()

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = _solicitudesFlow.value.find { it.id == id }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Boolean {
        val equipo = obtenerEquipo(solicitud.equipoId)
        return if (equipo != null && equipo.estado == EstadoEquipo.DISPONIBLE) {
            // Actualizar lista de solicitudes
            val nuevasSolicitudes = _solicitudesFlow.value.toMutableList()
            nuevasSolicitudes.add(solicitud.copy(estado = EstadoSolicitud.SOLICITADA))
            _solicitudesFlow.value = nuevasSolicitudes

            // Actualizar estado del equipo
            val nuevosEquipos = _equiposFlow.value.toMutableList()
            val idx = nuevosEquipos.indexOf(equipo)
            if (idx != -1) {
                nuevosEquipos[idx] = equipo.copy(estado = EstadoEquipo.RESERVADO)
                _equiposFlow.value = nuevosEquipos
            }
            true
        } else {
            false
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Boolean {
        val solicitud = obtenerSolicitud(id)
        return if (solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA) {
            // Cambiar estado de la solicitud a CANCELADA
            val nuevasSolicitudes = _solicitudesFlow.value.toMutableList()
            val index = nuevasSolicitudes.indexOf(solicitud)
            if (index != -1) {
                nuevasSolicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
                _solicitudesFlow.value = nuevasSolicitudes
            }

            // Liberar el equipo
            val equipo = obtenerEquipo(solicitud.equipoId)
            if (equipo != null) {
                val nuevosEquipos = _equiposFlow.value.toMutableList()
                val idx = nuevosEquipos.indexOf(equipo)
                if (idx != -1) {
                    nuevosEquipos[idx] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
                    _equiposFlow.value = nuevosEquipos
                }
            }
            true
        } else {
            false
        }
    }
}