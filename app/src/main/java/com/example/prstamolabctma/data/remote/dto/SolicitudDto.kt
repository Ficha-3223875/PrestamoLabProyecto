package com.example.prstamolabctma.data.remote.dto

import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

data class SolicitudDto(
    val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val evidenciaUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)

fun SolicitudDto.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = try { EstadoSolicitud.valueOf(estado) } catch (_: Exception) { EstadoSolicitud.SOLICITADA },
        evidenciaUri = evidenciaUri,
        latitud = latitud,
        longitud = longitud
    )
}

fun SolicitudPrestamo.toDto(): SolicitudDto {
    return SolicitudDto(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado.name,
        evidenciaUri = evidenciaUri,
        latitud = latitud,
        longitud = longitud
    )
}