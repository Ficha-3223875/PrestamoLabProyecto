package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudPrestamoDao
import com.example.prstamolabctma.data.local.entity.toDomain
import com.example.prstamolabctma.data.local.entity.toEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class OfflineFirstPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudPrestamoDao
) : PrestamoRepository {

    override fun obtenerEquipos(): Flow<List<Equipo>> {
        return equipoDao.getAllEquipos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerEquipo(id: Int): Flow<Equipo?> {
        return equipoDao.getEquipoById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.getAllSolicitudes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerSolicitud(id: Int): Flow<SolicitudPrestamo?> {
        return solicitudDao.getSolicitudById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        return try {
            // 1. Guardar la solicitud en Room
            solicitudDao.insertSolicitud(solicitud.toEntity())

            // 2. Cambiar el estado del equipo a PRESTADO
            equipoDao.updateEstadoEquipo(
                equipoId = solicitud.equipoId,
                nuevoEstado = EstadoEquipo.PRESTADO.name
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        return try {
            // 1. Obtener la solicitud para conocer el equipoId antes de cancelar
            val solicitud = solicitudDao.getSolicitudById(id).firstOrNull()

            if (solicitud != null) {
                // 2. Marcar la solicitud como CANCELADA
                solicitudDao.registrarDevolucion(
                    solicitudId = id,
                    nuevoEstado = "CANCELADA",
                    evidenciaUri = null
                )

                // 3. Devolver el estado del equipo a DISPONIBLE
                equipoDao.updateEstadoEquipo(
                    equipoId = solicitud.equipoId,
                    nuevoEstado = EstadoEquipo.DISPONIBLE.name
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}