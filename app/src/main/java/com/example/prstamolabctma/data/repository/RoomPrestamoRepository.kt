package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudDao
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import com.example.prstamolabctma.data.local.entity.SolicitudEntity
import com.example.prstamolabctma.data.remote.ApiService
import com.example.prstamolabctma.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val apiService: ApiService? = null // Opcional para pruebas o modo sin conexión
) : PrestamoRepository {

    suspend fun sembrarSiVacio() {
        if (equipoDao.obtenerEquipos().first().isEmpty()) {
            equipoDao.insertarEquipos(
                listOf(
                    EquipoEntity(1, "Multímetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
                    EquipoEntity(2, "Cámara Canon", CategoriaEquipo.CAMARA, EstadoEquipo.DISPONIBLE),
                    EquipoEntity(3, "Tablet Samsung", CategoriaEquipo.TABLETA, EstadoEquipo.DISPONIBLE)
                )
            )
        }
    }

    /**
     * Sincroniza los equipos desde la API remota hacia la base de datos local Room (Local-First)
     */
    suspend fun sincronizarEquipos() {
        try {
            val response = apiService?.obtenerEquiposRemotos()
            if (response != null && response.isSuccessful) {
                response.body()?.let { listaDto ->
                    val entidades = listaDto.map { dto ->
                        val dominio = dto.toDomain()
                        EquipoEntity(
                            id = dominio.id,
                            nombre = dominio.nombre,
                            categoria = dominio.categoria,
                            estado = dominio.estado
                        )
                    }
                    equipoDao.insertarEquipos(entidades)
                }
            }
        } catch (e: Exception) {
            // Error de red controlado
        }
    }

    override fun obtenerEquipos(): Flow<List<Equipo>> =
        equipoDao.obtenerEquipos().map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerEquipo(id)?.toDomain()

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.obtenerSolicitudes().map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudDao.obtenerSolicitud(id)?.toDomain()

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Boolean {
        val equipo = equipoDao.obtenerEquipo(solicitud.equipoId) ?: return false
        if (equipo.estado != EstadoEquipo.DISPONIBLE) return false

        val nuevaSolicitudEntity = SolicitudEntity(
            equipoId = solicitud.equipoId,
            ambienteDestino = solicitud.ambienteDestino,
            proposito = solicitud.proposito,
            duracionHoras = solicitud.duracionHoras,
            estado = EstadoSolicitud.SOLICITADA,
            evidenciaUri = solicitud.evidenciaUri,                 // 👈 Guardando evidencia fotográfica (Guía 9)
            dispositivoBluetooth = solicitud.dispositivoBluetooth // 👈 Guardando Bluetooth detectado (Guía 9)
        )

        solicitudDao.insertarSolicitud(nuevaSolicitudEntity)
        equipoDao.actualizarEquipo(equipo.copy(estado = EstadoEquipo.RESERVADO))

        // Opcional: Sincronizar creación con la API remota de manera asíncrona si hay conectividad
        return true
    }

    override suspend fun cancelarSolicitud(id: Int): Boolean {
        val solicitud = solicitudDao.obtenerSolicitud(id) ?: return false
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) return false

        solicitudDao.actualizarSolicitud(solicitud.copy(estado = EstadoSolicitud.CANCELADA))
        equipoDao.obtenerEquipo(solicitud.equipoId)?.let {
            equipoDao.actualizarEquipo(it.copy(estado = EstadoEquipo.DISPONIBLE))
        }
        return true
    }

    private fun EquipoEntity.toDomain() = Equipo(id, nombre, categoria, estado)

    // 👇 Mapeadores actualizados para soportar los campos de la Guía 9
    private fun SolicitudEntity.toDomain() = SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        evidenciaUri = evidenciaUri,
        dispositivoBluetooth = dispositivoBluetooth
    )
}