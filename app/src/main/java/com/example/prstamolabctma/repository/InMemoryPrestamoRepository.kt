package com.example.miformacionctma.repository

import com.example.miformacionctma.model.CategoriaEquipo
import com.example.miformacionctma.model.Equipo
import com.example.miformacionctma.model.EstadoEquipo
import com.example.miformacionctma.model.EstadoSolicitud
import com.example.miformacionctma.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(
            id = 1,
            nombre = "Multímetro Digital",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE
        ),
        Equipo(
            id = 2,
            nombre = "Cámara Digital",
            categoria = CategoriaEquipo.AUDIOVISUAL,
            estado = EstadoEquipo.DISPONIBLE
        ),
        Equipo(
            id = 3,
            nombre = "Computador Portátil",
            categoria = CategoriaEquipo.COMPUTACION,
            estado = EstadoEquipo.RESERVADO
        ),
        Equipo(
            id = 4,
            nombre = "Taladro",
            categoria = CategoriaEquipo.HERRAMIENTAS,
            estado = EstadoEquipo.DISPONIBLE
        ),
        Equipo(
            id = 5,
            nombre = "Tablet",
            categoria = CategoriaEquipo.COMPUTACION,
            estado = EstadoEquipo.PRESTADO
        )
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    private var siguienteSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> {
        return equipos.toList()
    }

    override fun obtenerEquipo(id: Int): Equipo? {
        return equipos.find { it.id == id }
    }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> {
        return solicitudes.toList()
    }

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudes.find { it.id == id }
    }

    override fun crearSolicitud(
        solicitud: SolicitudPrestamo
    ): Result<Unit> {

        val equipo = obtenerEquipo(solicitud.equipoId)
            ?: return Result.failure(
                IllegalArgumentException("El equipo no existe")
            )

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(
                IllegalStateException("El equipo no está disponible")
            )
        }

        val solicitudExistente = solicitudes.any {
            it.equipoId == solicitud.equipoId &&
                    it.estado == EstadoSolicitud.SOLICITADA
        }

        if (solicitudExistente) {
            return Result.failure(
                IllegalStateException("Ya existe una solicitud activa para este equipo")
            )
        }

        val nuevaSolicitud = solicitud.copy(
            id = siguienteSolicitudId++
        )

        solicitudes.add(nuevaSolicitud)

        val indiceEquipo = equipos.indexOfFirst {
            it.id == solicitud.equipoId
        }

        if (indiceEquipo != -1) {
            equipos[indiceEquipo] = equipo.copy(
                estado = EstadoEquipo.RESERVADO
            )
        }

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {

        val indiceSolicitud = solicitudes.indexOfFirst {
            it.id == id
        }

        if (indiceSolicitud == -1) {
            return Result.failure(
                IllegalArgumentException("La solicitud no existe")
            )
        }

        val solicitud = solicitudes[indiceSolicitud]

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(
                IllegalStateException(
                    "Solo se pueden cancelar solicitudes SOLICITADAS"
                )
            )
        }

        solicitudes[indiceSolicitud] = solicitud.copy(
            estado = EstadoSolicitud.CANCELADA
        )

        val indiceEquipo = equipos.indexOfFirst {
            it.id == solicitud.equipoId
        }

        if (indiceEquipo != -1) {
            equipos[indiceEquipo] = equipos[indiceEquipo].copy(
                estado = EstadoEquipo.DISPONIBLE
            )
        }

        return Result.success(Unit)
    }
}