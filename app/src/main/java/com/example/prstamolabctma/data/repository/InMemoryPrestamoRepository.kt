package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.*

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(1, "Multímetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Cámara Canon", CategoriaEquipo.CAMARA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Tablet Samsung", CategoriaEquipo.TABLETA, EstadoEquipo.DISPONIBLE)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    override fun obtenerEquipos(): List<Equipo> = equipos

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Boolean {
        val equipo = obtenerEquipo(solicitud.equipoId)
        return if (equipo != null && equipo.estado == EstadoEquipo.DISPONIBLE) {
            // Crear solicitud en estado SOLICITADA
            solicitudes.add(solicitud.copy(estado = EstadoSolicitud.SOLICITADA))
            // Cambiar estado del equipo a RESERVADO
            val idx = equipos.indexOf(equipo)
            equipos[idx] = equipo.copy(estado = EstadoEquipo.RESERVADO)
            true
        } else {
            false
        }
    }

    override fun cancelarSolicitud(id: Int): Boolean {
        val solicitud = obtenerSolicitud(id)
        return if (solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA) {
            // Cambiar estado de la solicitud a CANCELADA
            val index = solicitudes.indexOf(solicitud)
            solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
            // Liberar el equipo
            val equipo = obtenerEquipo(solicitud.equipoId)
            if (equipo != null) {
                val idx = equipos.indexOf(equipo)
                equipos[idx] = equipo.copy(estado = EstadoEquipo.DISPONIBLE)
            }
            true
        } else {
            false
        }
    }
}
