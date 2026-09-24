package com.example.prstamolabctma.data.remote.dto

import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

data class SolicitudDto(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String
) {
    fun toDomain(): SolicitudPrestamo {
        return SolicitudPrestamo(
            id = id,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino,
            proposito = proposito,
            duracionHoras = duracionHoras,
            estado = try {
                EstadoSolicitud.valueOf(estado)
            } catch (e: Exception) {
                EstadoSolicitud.SOLICITADA
            }
        )
    }
}