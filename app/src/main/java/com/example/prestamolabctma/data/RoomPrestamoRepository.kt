package com.example.prestamolabctma.data

import com.example.prestamolabctma.data.local.BaseDatosLocal
import com.example.prestamolabctma.data.local.Mappers.toDomain
import com.example.prestamolabctma.data.local.SolicitudPrestamoEntity
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPrestamoRepository(private val db: BaseDatosLocal) : PrestamoRepository {

    // El DAO expone reactividad y Room/Helper invalida o notifica tras cada cambio
    val equiposFlow: Flow<List<Equipo>> = db.cambiosNotifier.map {
        db.listarEquiposRaw().map { it.toDomain() }
    }

    val solicitudesFlow: Flow<List<SolicitudPrestamo>> = db.cambiosNotifier.map {
        db.listarSolicitudesRaw().map { it.toDomain() }
    }

    override fun listarEquipos(): List<Equipo> {
        return db.listarEquiposRaw().map { it.toDomain() }
    }

    override fun obtenerEquipo(id: Int): Equipo? {
        return db.obtenerEquipoRaw(id)?.toDomain()
    }

    override fun listarSolicitudes(): List<SolicitudPrestamo> {
        return db.listarSolicitudesRaw().map { it.toDomain() }
    }

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return db.obtenerSolicitudRaw(id)?.toDomain()
    }

    override fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> {

        val equipo = obtenerEquipo(equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo no existe."))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("El equipo no está disponible."))
        }

        if (ambienteDestino.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("El ambiente destino es obligatorio."))
        }

        if (proposito.trim().length !in 10..180) {
            return Result.failure(IllegalArgumentException("El propósito debe tener entre 10 y 180 caracteres."))
        }

        if (duracionHoras !in 1..8) {
            return Result.failure(IllegalArgumentException("La duración debe estar entre 1 y 8 horas."))
        }

        val solicitudesActivas = listarSolicitudes()
        val solicitudDuplicada = solicitudesActivas.any {
            it.equipoId == equipoId && it.estado in listOf(
                EstadoSolicitud.SOLICITADA, EstadoSolicitud.APROBADA, EstadoSolicitud.ENTREGADA
            )
        }

        if (solicitudDuplicada) {
            return Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
        }

        // Crear entidad local persistente con campo de versión 2 (fechaRegistro)
        val entity = SolicitudPrestamoEntity(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = "SOLICITADA",
            fechaRegistro = "2026-09-22" // Valor de esquema v2
        )

        val nuevoId = db.insertarSolicitudRaw(entity).toInt()
        db.actualizarEstadoEquipoRaw(equipoId, "RESERVADO")

        val guardada = db.obtenerSolicitudRaw(nuevoId)?.toDomain()
            ?: return Result.failure(IllegalStateException("Error al guardar en base de datos."))

        return Result.success(guardada)
    }

    override fun cancelarSolicitud(solicitudId: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return Result.failure(IllegalArgumentException("La solicitud no existe."))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se puede cancelar una solicitud SOLICITADA."))
        }

        db.actualizarEstadoSolicitudRaw(solicitudId, "CANCELADA")
        db.actualizarEstadoEquipoRaw(id = solicitud.equipoId, nuevoEstado = "DISPONIBLE")

        return Result.success(Unit)
    }
}
