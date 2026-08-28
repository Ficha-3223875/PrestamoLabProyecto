package com.example.prestamolabctma.data

import com.example.prestamolabctma.model.Equipo
import com.example.prestamolabctma.model.SolicitudPrestamo

interface PrestamoRepository {

    fun listarEquipos(): List<Equipo>

    fun obtenerEquipo(id: Int): Equipo?

    fun listarSolicitudes(): List<SolicitudPrestamo>

    fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo>

    fun cancelarSolicitud(
        solicitudId: Int
    ): Result<Unit>
}