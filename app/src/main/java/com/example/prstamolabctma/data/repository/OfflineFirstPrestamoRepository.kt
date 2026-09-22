package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudPrestamoDao
import com.example.prstamolabctma.data.local.entity.toDomain
import com.example.prstamolabctma.data.local.entity.toEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
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

    override suspend fun obtenerEquipo(id: Int): Equipo? {
        return equipoDao.getEquipoById(id)?.toDomain()
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.getAllSolicitudes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudDao.getSolicitudById(id)?.toDomain()
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        return try {
            solicitudDao.insertSolicitud(solicitud.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        return try {
            // Actualiza el estado usando los métodos de tu DAO
            solicitudDao.registrarDevolucion(
                solicitudId = id,
                nuevoEstado = "CANCELADA",
                evidenciaUri = null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}