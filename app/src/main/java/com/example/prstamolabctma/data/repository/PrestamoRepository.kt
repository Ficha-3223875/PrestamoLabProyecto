package com.example.prstamolabctma.data.repository

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo

interface PrestamoRepository {

    fun obtenerEquipos(): List<Equipo>

    fun obtenerEquipo(id: Int): Equipo?

    fun obtenerSolicitudes(): List<SolicitudPrestamo>

    fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>

    fun cancelarSolicitud(id: Int): Result<Unit>
}