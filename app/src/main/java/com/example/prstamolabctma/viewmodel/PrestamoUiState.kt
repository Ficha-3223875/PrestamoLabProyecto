package com.example.prstamolabctma.viewmodel

import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,   // ✅ aquí guardamos mensajes de error o confirmación
    val guardando: Boolean = false
)
