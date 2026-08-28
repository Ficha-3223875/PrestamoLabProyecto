package com.example.prestamolabctma.data

import com.example.prestamolabctma.model.CategoriaEquipo
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(

        Equipo(
            id = 1,
            nombre = "Kit Arduino UNO",
            categoria = CategoriaEquipo.ELECTRONICA,
            descripcion = "Kit para prácticas de electrónica y programación.",
            estado = EstadoEquipo.DISPONIBLE
        ),

        Equipo(
            id = 2,
            nombre = "Portátil Lenovo",
            categoria = CategoriaEquipo.COMPUTO,
            descripcion = "Equipo portátil para actividades académicas.",
            estado = EstadoEquipo.DISPONIBLE
        ),

        Equipo(
            id = 3,
            nombre = "Multímetro Digital",
            categoria = CategoriaEquipo.MEDICION,
            descripcion = "Instrumento para realizar mediciones eléctricas.",
            estado = EstadoEquipo.DISPONIBLE
        ),

        Equipo(
            id = 4,
            nombre = "Proyector Epson",
            categoria = CategoriaEquipo.AUDIOVISUAL,
            descripcion = "Proyector para presentaciones y clases.",
            estado = EstadoEquipo.RESERVADO
        ),

        Equipo(
            id = 5,
            nombre = "Taladro Eléctrico",
            categoria = CategoriaEquipo.HERRAMIENTA,
            descripcion = "Herramienta para prácticas de mantenimiento.",
            estado = EstadoEquipo.DISPONIBLE
        )
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    private var siguienteId = 1

    override fun listarEquipos(): List<Equipo> {
        return equipos.toList()
    }

    override fun obtenerEquipo(id: Int): Equipo? {
        return equipos.find { it.id == id }
    }

    override fun listarSolicitudes(): List<SolicitudPrestamo> {
        return solicitudes.toList()
    }

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudes.find { it.id == id }
    }

    override fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> {

        val equipo = obtenerEquipo(equipoId)
            ?: return Result.failure(
                IllegalArgumentException("El equipo no existe.")
            )

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(
                IllegalStateException(
                    "El equipo no está disponible."
                )
            )
        }

        if (ambienteDestino.trim().isEmpty()) {
            return Result.failure(
                IllegalArgumentException(
                    "El ambiente destino es obligatorio."
                )
            )
        }

        if (proposito.trim().length !in 10..180) {
            return Result.failure(
                IllegalArgumentException(
                    "El propósito debe tener entre 10 y 180 caracteres."
                )
            )
        }

        if (duracionHoras !in 1..8) {
            return Result.failure(
                IllegalArgumentException(
                    "La duración debe estar entre 1 y 8 horas."
                )
            )
        }

        val solicitudDuplicada = solicitudes.any {

            it.equipoId == equipoId &&

                    it.estado in listOf(
                EstadoSolicitud.SOLICITADA,
                EstadoSolicitud.APROBADA,
                EstadoSolicitud.ENTREGADA
            )
        }

        if (solicitudDuplicada) {

            return Result.failure(
                IllegalStateException(
                    "Ya existe una solicitud activa para este equipo."
                )
            )
        }

        val solicitud = SolicitudPrestamo(

            id = siguienteId++,

            equipoId = equipoId,

            ambienteDestino = ambienteDestino.trim(),

            proposito = proposito.trim(),

            duracionHoras = duracionHoras,

            estado = EstadoSolicitud.SOLICITADA
        )

        solicitudes.add(solicitud)

        val posicionEquipo =
            equipos.indexOfFirst { it.id == equipoId }

        equipos[posicionEquipo] =
            equipo.copy(
                estado = EstadoEquipo.RESERVADO
            )

        return Result.success(solicitud)
    }

    override fun cancelarSolicitud(
        solicitudId: Int
    ): Result<Unit> {

        val solicitud =
            obtenerSolicitud(solicitudId)
                ?: return Result.failure(
                    IllegalArgumentException(
                        "La solicitud no existe."
                    )
                )

        if (
            solicitud.estado !=
            EstadoSolicitud.SOLICITADA
        ) {

            return Result.failure(
                IllegalStateException(
                    "Solo se puede cancelar una solicitud SOLICITADA."
                )
            )
        }

        val posicionSolicitud =
            solicitudes.indexOfFirst {
                it.id == solicitudId
            }

        solicitudes[posicionSolicitud] =
            solicitud.copy(
                estado = EstadoSolicitud.CANCELADA
            )

        val equipo =
            obtenerEquipo(solicitud.equipoId)

        if (equipo != null) {

            val posicionEquipo =
                equipos.indexOfFirst {
                    it.id == equipo.id
                }

            equipos[posicionEquipo] =
                equipo.copy(
                    estado = EstadoEquipo.DISPONIBLE
                )
        }

        return Result.success(Unit)
    }
}