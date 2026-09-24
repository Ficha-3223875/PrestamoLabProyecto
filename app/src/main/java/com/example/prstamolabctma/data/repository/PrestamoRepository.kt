package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {

    fun obtenerEquipos(): Flow<List<Equipo>>

    fun obtenerEquipo(id: Int): Flow<Equipo?>

    fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>>

    fun obtenerSolicitud(id: Int): Flow<SolicitudPrestamo?>

    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>

    suspend fun cancelarSolicitud(id: Int): Result<Unit>
}