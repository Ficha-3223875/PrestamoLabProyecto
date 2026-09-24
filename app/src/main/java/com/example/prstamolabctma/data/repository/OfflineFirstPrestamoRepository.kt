package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudPrestamoDao
import com.example.prstamolabctma.data.local.entity.toDomain
import com.example.prstamolabctma.data.local.entity.toEntity
import com.example.prstamolabctma.data.remote.api.PrestamoApiService
import com.example.prstamolabctma.data.remote.dto.toDto
import com.example.prstamolabctma.data.remote.dto.toEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class OfflineFirstPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudPrestamoDao,
    private val apiService: PrestamoApiService
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

    // Función de sincronización remota opcional para actualizar el caché local desde la API
    suspend fun sincronizarEquipos(): Result<Unit> {
        return try {
            val response = apiService.getEquipos()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val entities = dtos.map { it.toEntity() }
                    equipoDao.insertEquipos(entities)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error de servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        return try {
            // 1. Guardar localmente en Room
            solicitudDao.insertSolicitud(solicitud.toEntity())

            // 2. Cambiar estado del equipo en Room a PRESTADO
            equipoDao.updateEstadoEquipo(
                equipoId = solicitud.equipoId,
                nuevoEstado = EstadoEquipo.PRESTADO.name
            )

            // 3. Sincronizar en segundo plano con la API REST (sin bloquear si falla la red)
            try {
                apiService.crearSolicitud(solicitud.toDto())
            } catch (_: Exception) {
                // Si la red falla, la app sigue funcionando offline
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        return try {
            val solicitud = solicitudDao.getSolicitudById(id).firstOrNull()

            if (solicitud != null) {
                // 1. Actualizar en Room
                solicitudDao.registrarDevolucion(
                    solicitudId = id,
                    nuevoEstado = "CANCELADA",
                    evidenciaUri = null
                )

                equipoDao.updateEstadoEquipo(
                    equipoId = solicitud.equipoId,
                    nuevoEstado = EstadoEquipo.DISPONIBLE.name
                )

                // 2. Notificar la cancelación al servicio remoto
                try {
                    apiService.cancelarSolicitud(id)
                } catch (_: Exception) {
                    // Tolerancia a fallos de conexión
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}