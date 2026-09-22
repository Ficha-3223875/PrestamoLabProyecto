package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo

interface PrestamoRepository {
    suspend fun obtenerEquipos(): List<Equipo>
    suspend fun obtenerEquipo(id: Int): Equipo?
    suspend fun obtenerSolicitudes(): List<SolicitudPrestamo>
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Boolean
    suspend fun cancelarSolicitud(id: Int): Boolean
}