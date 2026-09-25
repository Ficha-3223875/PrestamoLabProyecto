package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.SolicitudPrestamoDao
import com.example.prstamolabctma.data.local.entity.SolicitudPrestamoEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import com.example.prstamolabctma.model.EstadoSolicitud // Asegúrate que este sea tu import de enum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPrestamoRepository(
    private val solicitudDao: SolicitudPrestamoDao
) : PrestamoRepository {

    override fun obtenerEquipos(): Flow<List<Equipo>> {
        throw NotImplementedError("La gestión de equipos se mantiene operativa desde el origen definido.")
    }

    override fun obtenerEquipo(id: Int): Flow<Equipo?> {
        throw NotImplementedError("La gestión de equipos se mantiene operativa desde el origen definido.")
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.getAllSolicitudes().map { entities ->
            entities.map { entity ->
                SolicitudPrestamo(
                    id = entity.id,
                    equipoId = entity.equipoId,
                    ambienteDestino = entity.ambienteDestino,
                    proposito = entity.proposito,
                    duracionHoras = entity.duracionHoras,
                    estado = try { EstadoSolicitud.valueOf(entity.estado) } catch (e: Exception) { EstadoSolicitud.SOLICITADA },
                    evidenciaUri = entity.evidenciaUri,
                    latitud = entity.latitud,
                    longitud = entity.longitud
                )
            }
        }
    }

    override fun obtenerSolicitud(id: Int): Flow<SolicitudPrestamo?> {
        return solicitudDao.getSolicitudById(id).map { entity ->
            entity?.let {
                SolicitudPrestamo(
                    id = it.id,
                    equipoId = it.equipoId,
                    ambienteDestino = it.ambienteDestino,
                    proposito = it.proposito,
                    duracionHoras = it.duracionHoras,
                    estado = try { EstadoSolicitud.valueOf(it.estado) } catch (e: Exception) { EstadoSolicitud.SOLICITADA },
                    evidenciaUri = it.evidenciaUri,
                    latitud = it.latitud,
                    longitud = it.longitud
                )
            }
        }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        return try {
            val entity = SolicitudPrestamoEntity(
                id = solicitud.id,
                equipoId = solicitud.equipoId,
                ambienteDestino = solicitud.ambienteDestino,
                proposito = solicitud.proposito,
                duracionHoras = solicitud.duracionHoras,
                estado = solicitud.estado.name, // Convertimos el Enum a String para la Entity
                evidenciaUri = solicitud.evidenciaUri,
                latitud = solicitud.latitud,
                longitud = solicitud.longitud
            )
            solicitudDao.insertSolicitud(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        return try {
            solicitudDao.registrarDevolucion(
                solicitudId = id,
                nuevoEstado = EstadoSolicitud.CANCELADA.name,
                evidenciaUri = null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}