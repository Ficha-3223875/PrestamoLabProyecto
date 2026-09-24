package com.example.prstamolabctma.data.remote.dto

import com.example.prstamolabctma.data.local.entity.SolicitudPrestamoEntity
import com.example.prstamolabctma.model.EstadoSolicitud
import com.example.prstamolabctma.model.SolicitudPrestamo

data class SolicitudDto(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String
)

// DTO -> Modelo de Dominio
fun SolicitudDto.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = runCatching { EstadoSolicitud.valueOf(estado) }.getOrDefault(EstadoSolicitud.SOLICITADA)
    )
}

// DTO -> Entidad de Room
fun SolicitudDto.toEntity(): SolicitudPrestamoEntity {
    return SolicitudPrestamoEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado
    )
}

// Modelo de Dominio -> DTO
fun SolicitudPrestamo.toDto(): SolicitudDto {
    return SolicitudDto(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado.name
    )
}