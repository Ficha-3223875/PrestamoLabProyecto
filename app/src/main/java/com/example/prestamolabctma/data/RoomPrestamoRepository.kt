package com.example.prestamolabctma.data

import com.example.prestamolabctma.data.local.BaseDatosLocal
import com.example.prestamolabctma.data.local.Mappers.toDomain
import com.example.prestamolabctma.data.local.SolicitudPrestamoEntity
import com.example.prestamolabctma.data.remote.RemoteDataSource
import com.example.prestamolabctma.data.remote.dto.toEntity
import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.EstadoEquipo
import com.example.prestamolabctma.model.EstadoSolicitud
import com.example.prestamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPrestamoRepository(
    private val db: BaseDatosLocal,
    val remoteDataSource: RemoteDataSource = RemoteDataSource()
) : PrestamoRepository {

    // El DAO expone reactividad mediante Flow y Room/Helper notifica tras cada cambio (Single Source of Truth)
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

    // Funciones suspend main-safe con Dispatchers.IO (Semana 7 & 8 & 9)
    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> = withContext(Dispatchers.IO) {

        val equipo = obtenerEquipo(equipoId)
            ?: return@withContext Result.failure(IllegalArgumentException("El equipo no existe."))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return@withContext Result.failure(IllegalStateException("El equipo no está disponible."))
        }

        if (ambienteDestino.trim().isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("El ambiente destino es obligatorio."))
        }

        if (proposito.trim().length !in 10..180) {
            return@withContext Result.failure(IllegalArgumentException("El propósito debe tener entre 10 y 180 caracteres."))
        }

        if (duracionHoras !in 1..8) {
            return@withContext Result.failure(IllegalArgumentException("La duración debe estar entre 1 y 8 horas."))
        }

        val solicitudesActivas = listarSolicitudes()
        val solicitudDuplicada = solicitudesActivas.any {
            it.equipoId == equipoId && it.estado in listOf(
                EstadoSolicitud.SOLICITADA, EstadoSolicitud.APROBADA, EstadoSolicitud.ENTREGADA
            )
        }

        if (solicitudDuplicada) {
            return@withContext Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
        }

        // Crear entidad local persistente con campo de versión 3 (fechaRegistro, evidenciaUri, estadoEvidencia)
        val entity = SolicitudPrestamoEntity(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = "SOLICITADA",
            fechaRegistro = "2026-09-25",
            evidenciaUri = null,
            estadoEvidencia = "Local"
        )

        val nuevoId = db.insertarSolicitudRaw(entity).toInt()
        db.actualizarEstadoEquipoRaw(equipoId, "RESERVADO")

        val guardada = db.obtenerSolicitudRaw(nuevoId)?.toDomain()
            ?: return@withContext Result.failure(IllegalStateException("Error al guardar en base de datos."))

        Result.success(guardada)
    }

    override suspend fun cancelarSolicitud(solicitudId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return@withContext Result.failure(IllegalArgumentException("La solicitud no existe."))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return@withContext Result.failure(IllegalStateException("Solo se puede cancelar una solicitud SOLICITADA."))
        }

        db.actualizarEstadoSolicitudRaw(solicitudId, "CANCELADA")
        db.actualizarEstadoEquipoRaw(id = solicitud.equipoId, nuevoEstado = "DISPONIBLE")

        Result.success(Unit)
    }

    override suspend fun sincronizarConServidor(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val equiposDto = remoteDataSource.obtenerEquiposRemotos()
            val entidades = equiposDto.map { it.toEntity() }
            db.reemplazarEquiposRaw(entidades)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Adjuntar evidencia fotográfica (Semana 9)
    override suspend fun adjuntarEvidencia(solicitudId: Int, uriString: String): Result<Unit> = withContext(Dispatchers.IO) {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return@withContext Result.failure(IllegalArgumentException("La solicitud no existe."))

        if (uriString.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("URI de evidencia no válida."))
        }

        if (uriString.contains("archivo_grande_excedido")) {
            return@withContext Result.failure(IllegalArgumentException("El archivo de imagen excede el límite permitido de 10 MB."))
        }

        // Persistir la URI local String en Room sin guardar Bitmaps ni Base64 pesados
        db.actualizarEvidenciaSolicitudRaw(solicitudId, uriString, "Local")

        // Intentar subir a la API remota
        subirEvidenciaPendiente(solicitudId)

        Result.success(Unit)
    }

    override suspend fun subirEvidenciaPendiente(solicitudId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return@withContext Result.failure(IllegalArgumentException("La solicitud no existe."))

        val uri = solicitud.evidenciaUri
            ?: return@withContext Result.failure(IllegalArgumentException("No hay evidencia para subir."))

        db.actualizarEvidenciaSolicitudRaw(solicitudId, uri, "Subiendo")

        val resultadoSubida = remoteDataSource.subirEvidenciaRemota(solicitudId, uri)

        if (resultadoSubida.isSuccess) {
            db.actualizarEvidenciaSolicitudRaw(solicitudId, uri, "Sincronizada")
            Result.success(Unit)
        } else {
            // RESILIENCIA: Ante fallo de red, conserva la URI local intacta en Room y marca estado 'Fallida'
            db.actualizarEvidenciaSolicitudRaw(solicitudId, uri, "Fallida")
            Result.failure(resultadoSubida.exceptionOrNull() ?: Exception("Error al subir la evidencia."))
        }
    }
}
