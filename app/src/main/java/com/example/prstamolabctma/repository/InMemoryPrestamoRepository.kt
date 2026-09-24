package com.example.prstamolabctma.repository

import com.example.prstamolabctma.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryPrestamoRepository : PrestamoRepository {
    private val equipos = mutableListOf(
        Equipo(1, "Multímetro", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Laptop", CategoriaEquipo.INFORMATICA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Cámara", CategoriaEquipo.OTRO, EstadoEquipo.RESERVADO)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    private val _equiposFlow = MutableStateFlow<List<Equipo>>(equipos.toList())
    private val _solicitudesFlow = MutableStateFlow<List<SolicitudPrestamo>>(solicitudes.toList())

    private fun notifyChanges() {
        _equiposFlow.value = equipos.toList()
        _solicitudesFlow.value = solicitudes.toList()
    }

    override fun obtenerEquiposFlow(): Flow<List<Equipo>> = _equiposFlow.asStateFlow()
    override suspend fun obtenerEquipos(): List<Equipo> = equipos.toList()
    override suspend fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamo>> = _solicitudesFlow.asStateFlow()
    override suspend fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()
    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

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
        val solicitudDuplicada = solicitudes.any { it.equipoId == solicitud.equipoId && it.estado == EstadoSolicitud.SOLICITADA }
        if (solicitudDuplicada) {
            return Result.failure(Exception("Ya existe una solicitud activa para este equipo"))
        }

        val equipo = obtenerEquipo(solicitud.equipoId)
        return if (equipo != null && equipo.estado == EstadoEquipo.DISPONIBLE) {
            solicitudes.add(solicitud)
            equipo.estado = EstadoEquipo.RESERVADO
            notifyChanges()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Equipo no disponible"))
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(id)
        return if (solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA) {
            solicitud.estado = EstadoSolicitud.CANCELADA
            val equipo = obtenerEquipo(solicitud.equipoId)
            equipo?.estado = EstadoEquipo.DISPONIBLE
            notifyChanges()
            Result.success(Unit)
        } else {
            Result.failure(Exception("No se puede cancelar"))
        }
    }

    override suspend fun refrescarDatos(): Result<Unit> {
        return Result.success(Unit)
    }
}
