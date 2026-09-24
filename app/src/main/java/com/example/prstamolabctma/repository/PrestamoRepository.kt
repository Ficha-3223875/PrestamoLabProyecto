package com.example.prstamolabctma.repository

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun obtenerEquiposFlow(): Flow<List<Equipo>>
    suspend fun obtenerEquipos(): List<Equipo>
    suspend fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamo>>
    suspend fun obtenerSolicitudes(): List<SolicitudPrestamo>
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>
}
