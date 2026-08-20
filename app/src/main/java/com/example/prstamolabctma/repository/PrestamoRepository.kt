package com.example.miformacionctma.repository

import com.example.miformacionctma.model.Equipo
import com.example.miformacionctma.model.SolicitudPrestamo

interface PrestamoRepository {

    fun obtenerEquipos(): List<Equipo>

    fun obtenerEquipo(id: Int): Equipo?

    fun obtenerSolicitudes(): List<SolicitudPrestamo>

    fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>

    fun cancelarSolicitud(id: Int): Result<Unit>
}