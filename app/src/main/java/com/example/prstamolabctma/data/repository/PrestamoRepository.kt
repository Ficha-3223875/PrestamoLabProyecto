package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {

    fun obtenerEquipos(): Flow<List<Equipo>>

    suspend fun obtenerEquipo(id: Int): Equipo?

    fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>>

    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>

    suspend fun cancelarSolicitud(id: Int): Result<Unit>
}