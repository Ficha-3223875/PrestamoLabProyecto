package com.example.prstamolabctma.repository

import com.example.prstamolabctma.data.local.EquipoDao
import com.example.prstamolabctma.data.local.SolicitudDao
import com.example.prstamolabctma.data.local.toDomain
import com.example.prstamolabctma.data.local.toEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

    override fun obtenerEquiposFlow(): Flow<List<Equipo>> {
        return equipoDao.getEquiposFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerEquipos(): List<Equipo> {
        return equipoDao.getEquiposList().map { it.toDomain() }
    }

    override suspend fun obtenerEquipo(id: Int): Equipo? {
        return equipoDao.getEquipoById(id)?.toDomain()
    }

    override fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.getSolicitudesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerSolicitudes(): List<SolicitudPrestamo> {
        return solicitudDao.getSolicitudesList().map { it.toDomain() }
    }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudDao.getSolicitudById(id)?.toDomain()
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        // Validaciones de negocio
        if (solicitud.ambienteDestino.isBlank()) {
            return Result.failure(Exception("El destino es obligatorio"))
        }
        if (solicitud.proposito.length < 10 || solicitud.proposito.length > 180) {
            return Result.failure(Exception("El propósito debe tener entre 10 y 180 caracteres"))
        }
        if (solicitud.duracionHoras < 1 || solicitud.duracionHoras > 8) {
            return Result.failure(Exception("La duración debe estar entre 1 y 8 horas"))
        }

        // Verificar si ya existe una solicitud activa para este equipo
        val conteoActivas = solicitudDao.countSolicitudesActivasPorEquipo(
            equipoId = solicitud.equipoId,
            estado = EstadoSolicitud.SOLICITADA.name
        )
        if (conteoActivas > 0) {
            return Result.failure(Exception("Ya existe una solicitud activa para este equipo"))
        }

        val equipoEntity = equipoDao.getEquipoById(solicitud.equipoId)
        return if (equipoEntity != null && equipoEntity.estado == EstadoEquipo.DISPONIBLE.name) {
            solicitudDao.insertSolicitud(solicitud.toEntity())
            equipoDao.updateEstadoEquipo(solicitud.equipoId, EstadoEquipo.RESERVADO.name)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Equipo no disponible"))
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitudEntity = solicitudDao.getSolicitudById(id)
        return if (solicitudEntity != null && solicitudEntity.estado == EstadoSolicitud.SOLICITADA.name) {
            solicitudDao.updateEstadoSolicitud(id, EstadoSolicitud.CANCELADA.name)
            equipoDao.updateEstadoEquipo(solicitudEntity.equipoId, EstadoEquipo.DISPONIBLE.name)
            Result.success(Unit)
        } else {
            Result.failure(Exception("No se puede cancelar"))
        }
    }
}
