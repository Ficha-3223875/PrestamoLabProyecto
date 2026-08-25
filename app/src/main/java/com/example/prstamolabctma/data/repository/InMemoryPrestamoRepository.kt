package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

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
            nombre = "Taladro Eléctrico",
            categoria = CategoriaEquipo.HERRAMIENTA,
            estado = EstadoEquipo.PRESTADO
        ),
        Equipo(
            id = 4,
            nombre = "Tableta",
            categoria = CategoriaEquipo.COMPUTO,
            estado = EstadoEquipo.DISPONIBLE
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
                IllegalArgumentException("El equipo no existe.")
            )

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(
                IllegalStateException("El equipo no está disponible.")
            )
        }

        val solicitudActiva = solicitudes.any {
            it.equipoId == solicitud.equipoId &&
                    it.estado == EstadoSolicitud.SOLICITADA
        }

        if (solicitudActiva) {
            return Result.failure(
                IllegalStateException("Ya existe una solicitud activa para este equipo.")
            )
        }

        val nuevaSolicitud = solicitud.copy(
            id = siguienteSolicitudId++,
            estado = EstadoSolicitud.SOLICITADA
        )

        solicitudes.add(nuevaSolicitud)

        val indiceEquipo = equipos.indexOfFirst {
            it.id == equipo.id
        }

        if (indiceEquipo != -1) {
            equipos[indiceEquipo] =
                equipo.copy(estado = EstadoEquipo.RESERVADO)
        }

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {

        val indiceSolicitud = solicitudes.indexOfFirst {
            it.id == id
        }

        if (indiceSolicitud == -1) {
            return Result.failure(
                IllegalArgumentException("La solicitud no existe.")
            )
        }

        val solicitud = solicitudes[indiceSolicitud]

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(
                IllegalStateException(
                    "Solo se pueden cancelar solicitudes en estado SOLICITADA."
                )
            )
        }

        solicitudes[indiceSolicitud] =
            solicitud.copy(
                estado = EstadoSolicitud.CANCELADA
            )

        val indiceEquipo = equipos.indexOfFirst {
            it.id == solicitud.equipoId
        }

        if (indiceEquipo != -1) {
            equipos[indiceEquipo] =
                equipos[indiceEquipo].copy(
                    estado = EstadoEquipo.DISPONIBLE
                )
        }

        return Result.success(Unit)
    }
}