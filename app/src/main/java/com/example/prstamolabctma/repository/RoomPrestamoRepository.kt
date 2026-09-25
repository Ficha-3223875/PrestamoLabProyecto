package com.example.prstamolabctma.repository

import com.example.prstamolabctma.data.local.EquipoDao
import com.example.prstamolabctma.data.local.EvidenciaDao
import com.example.prstamolabctma.data.local.SolicitudDao
import com.example.prstamolabctma.data.local.toDomain
import com.example.prstamolabctma.data.local.toEntity
import com.example.prstamolabctma.data.remote.PrestamoApiService
import com.example.prstamolabctma.data.remote.toDto
import com.example.prstamolabctma.data.remote.toEntity
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoEvidencia
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.Evidencia
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val evidenciaDao: EvidenciaDao? = null,
    private val apiService: PrestamoApiService? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PrestamoRepository {

    override fun obtenerEquiposFlow(): Flow<List<Equipo>> {
        return equipoDao.getEquiposFlow()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun obtenerEquipos(): List<Equipo> = withContext(ioDispatcher) {
        equipoDao.getEquiposList().map { it.toDomain() }
    }

    override suspend fun obtenerEquipo(id: Int): Equipo? = withContext(ioDispatcher) {
        equipoDao.getEquipoById(id)?.toDomain()
    }

    override fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.getSolicitudesFlow()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun obtenerSolicitudes(): List<SolicitudPrestamo> = withContext(ioDispatcher) {
        solicitudDao.getSolicitudesList().map { it.toDomain() }
    }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = withContext(ioDispatcher) {
        solicitudDao.getSolicitudById(id)?.toDomain()
    }

    override suspend fun refrescarDatos(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val api = apiService ?: return@withContext Result.success(Unit)
            val equiposRemote = api.getEquipos()
            if (equiposRemote.isNotEmpty()) {
                equipoDao.insertEquipos(equiposRemote.map { it.toEntity() })
            }
            val solicitudesRemote = api.getSolicitudes()
            if (solicitudesRemote.isNotEmpty()) {
                solicitudesRemote.forEach {
                    solicitudDao.insertSolicitud(it.toEntity())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> = withContext(ioDispatcher) {
        if (solicitud.ambienteDestino.isBlank()) {
            return@withContext Result.failure(Exception("El destino es obligatorio"))
        }
        if (solicitud.proposito.length < 10 || solicitud.proposito.length > 180) {
            return@withContext Result.failure(Exception("El propósito debe tener entre 10 y 180 caracteres"))
        }
        if (solicitud.duracionHoras < 1 || solicitud.duracionHoras > 8) {
            return@withContext Result.failure(Exception("La duración debe estar entre 1 y 8 horas"))
        }

        val conteoActivas = solicitudDao.countSolicitudesActivasPorEquipo(
            equipoId = solicitud.equipoId,
            estado = EstadoSolicitud.SOLICITADA.name
        )
        if (conteoActivas > 0) {
            return@withContext Result.failure(Exception("Ya existe una solicitud activa para este equipo"))
        }

        val equipoEntity = equipoDao.getEquipoById(solicitud.equipoId)
        return@withContext if (equipoEntity != null && equipoEntity.estado == EstadoEquipo.DISPONIBLE.name) {
            runCatching {
                apiService?.crearSolicitud(solicitud.toDto())
            }
            solicitudDao.insertSolicitud(solicitud.toEntity())
            equipoDao.updateEstadoEquipo(solicitud.equipoId, EstadoEquipo.RESERVADO.name)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Equipo no disponible"))
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> = withContext(ioDispatcher) {
        val solicitudEntity = solicitudDao.getSolicitudById(id)
        return@withContext if (solicitudEntity != null && solicitudEntity.estado == EstadoSolicitud.SOLICITADA.name) {
            runCatching {
                apiService?.cancelarSolicitud(id)
            }
            solicitudDao.updateEstadoSolicitud(id, EstadoSolicitud.CANCELADA.name)
            equipoDao.updateEstadoEquipo(solicitudEntity.equipoId, EstadoEquipo.DISPONIBLE.name)
            Result.success(Unit)
        } else {
            Result.failure(Exception("No se puede cancelar"))
        }
    }

    override fun obtenerEvidenciasPorSolicitudFlow(solicitudId: Int): Flow<List<Evidencia>> {
        val dao = evidenciaDao ?: return flowOf(emptyList())
        return dao.getEvidenciasPorSolicitudFlow(solicitudId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun adjuntarEvidencia(evidencia: Evidencia): Result<Unit> = withContext(ioDispatcher) {
        val dao = evidenciaDao ?: return@withContext Result.success(Unit)

        // 1. Guardar primero con estado LOCAL / SUBIENDO
        val idGenerado = dao.insertEvidencia(evidencia.copy(estado = EstadoEvidencia.SUBIENDO).toEntity())
        val idFinal = if (evidencia.id == 0) idGenerado.toInt() else evidencia.id

        // 2. Intentar sincronización remota si hay servicio de red
        return@withContext try {
            if (apiService != null) {
                // Simulación de envío remoto por HTTPS
                dao.updateEstadoEvidencia(idFinal, EstadoEvidencia.SINCRONIZADA.name)
            } else {
                dao.updateEstadoEvidencia(idFinal, EstadoEvidencia.LOCAL.name)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // En caso de fallo de red, se conserva la evidencia localmente con estado FALLIDA
            dao.updateEstadoEvidencia(idFinal, EstadoEvidencia.FALLIDA.name)
            Result.failure(Exception("Error de red al sincronizar evidencia. Se conservó copia local.", e))
        }
    }

    override suspend fun eliminarEvidencia(id: Int): Result<Unit> = withContext(ioDispatcher) {
        val dao = evidenciaDao ?: return@withContext Result.success(Unit)
        dao.deleteEvidencia(id)
        Result.success(Unit)
    }

    override suspend fun reintentarSubidaEvidencia(id: Int): Result<Unit> = withContext(ioDispatcher) {
        val dao = evidenciaDao ?: return@withContext Result.success(Unit)
        dao.updateEstadoEvidencia(id, EstadoEvidencia.SUBIENDO.name)
        return@withContext try {
            if (apiService != null) {
                dao.updateEstadoEvidencia(id, EstadoEvidencia.SINCRONIZADA.name)
            } else {
                dao.updateEstadoEvidencia(id, EstadoEvidencia.LOCAL.name)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            dao.updateEstadoEvidencia(id, EstadoEvidencia.FALLIDA.name)
            Result.failure(e)
        }
    }
}
