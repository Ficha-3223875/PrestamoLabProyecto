package com.example.prstamolabctma

import com.example.prstamolabctma.data.repository.PrestamoRepository
import com.example.prstamolabctma.model.CategoriaEquipo
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.EstadoEquipo
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equiposList = mutableListOf(
        Equipo(1, "Multímetro Digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Cámara Digital", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Taladro Eléctrico", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.PRESTADO),
        Equipo(4, "Tableta", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE)
    )
    private val solicitudesList = mutableListOf<SolicitudPrestamo>()
    private var nextSolicitudId = 1

    private val equiposFlow = MutableStateFlow<List<Equipo>>(equiposList.toList())
    private val solicitudesFlow = MutableStateFlow<List<SolicitudPrestamo>>(solicitudesList.toList())

    override fun obtenerEquipos(): Flow<List<Equipo>> = equiposFlow

    override fun obtenerEquipo(id: Int): Flow<Equipo?> {
        return equiposFlow.map { list -> list.find { it.id == id } }
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> = solicitudesFlow

    override fun obtenerSolicitud(id: Int): Flow<SolicitudPrestamo?> {
        return solicitudesFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoIndex = equiposList.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1 && equiposList[equipoIndex].estado == EstadoEquipo.DISPONIBLE) {
            val nueva = solicitud.copy(id = nextSolicitudId++)
            solicitudesList.add(nueva)
            equiposList[equipoIndex] = equiposList[equipoIndex].copy(estado = EstadoEquipo.RESERVADO)

            equiposFlow.value = equiposList.toList()
            solicitudesFlow.value = solicitudesList.toList()
            return Result.success(Unit)
        }
        return Result.failure(IllegalStateException("El equipo no está disponible para préstamo."))
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solIndex = solicitudesList.indexOfFirst { it.id == id }
        if (solIndex != -1) {
            val sol = solicitudesList[solIndex]
            solicitudesList[solIndex] = sol.copy(estado = EstadoSolicitud.CANCELADA)

            val eqIndex = equiposList.indexOfFirst { it.id == sol.equipoId }
            if (eqIndex != -1) {
                equiposList[eqIndex] = equiposList[eqIndex].copy(estado = EstadoEquipo.DISPONIBLE)
            }

            equiposFlow.value = equiposList.toList()
            solicitudesFlow.value = solicitudesList.toList()
            return Result.success(Unit)
        }
        return Result.failure(IllegalStateException("Solicitud no encontrada."))
    }
}
