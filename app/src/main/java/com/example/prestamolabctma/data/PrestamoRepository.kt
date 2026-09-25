package com.example.prestamolabctma.data

import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo

interface PrestamoRepository {

    fun listarEquipos(): List<Equipo>

    fun obtenerEquipo(id: Int): Equipo?

    fun listarSolicitudes(): List<SolicitudPrestamo>

    fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo>

    suspend fun cancelarSolicitud(
        solicitudId: Int
    ): Result<Unit>

    suspend fun sincronizarConServidor(): Result<Unit>

    suspend fun adjuntarEvidencia(
        solicitudId: Int,
        uriString: String
    ): Result<Unit>

    suspend fun subirEvidenciaPendiente(
        solicitudId: Int
    ): Result<Unit>
}
