package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.data.local.dao.EquipoDao
import com.example.prstamolabctma.data.local.dao.SolicitudDao
import com.example.prstamolabctma.data.local.entity.EquipoEntity
import com.example.prstamolabctma.data.local.entity.SolicitudEntity
import com.example.prstamolabctma.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

    suspend fun sembrarSiVacio() {
        // Usamos .first() para leer la lista actual del Flow una vez al iniciar la app
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

    // 👈 Convertido a Flow con mapeo automático al modelo de dominio
    override fun obtenerEquipos(): Flow<List<Equipo>> =
        equipoDao.obtenerEquipos().map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerEquipo(id)?.toDomain()

    // 👈 Convertido a Flow con mapeo automático al modelo de dominio
    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.obtenerSolicitudes().map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudDao.obtenerSolicitud(id)?.toDomain()

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Boolean {
        val equipo = equipoDao.obtenerEquipo(solicitud.equipoId) ?: return false
        if (equipo.estado != EstadoEquipo.DISPONIBLE) return false

        solicitudDao.insertarSolicitud(
            SolicitudEntity(
                equipoId = solicitud.equipoId,
                ambienteDestino = solicitud.ambienteDestino,
                proposito = solicitud.proposito,
                duracionHoras = solicitud.duracionHoras,
                estado = EstadoSolicitud.SOLICITADA
            )
        )
        equipoDao.actualizarEquipo(equipo.copy(estado = EstadoEquipo.RESERVADO))
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
    private fun SolicitudEntity.toDomain() = SolicitudPrestamo(id, equipoId, ambienteDestino, proposito, duracionHoras, estado)
}