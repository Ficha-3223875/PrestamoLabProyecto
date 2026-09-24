package com.example.prstamolabctma.data.remote

import com.example.prstamolabctma.data.local.EquipoEntity
import com.example.prstamolabctma.data.local.SolicitudEntity
import com.example.prstamolabctma.data.local.toDomain
import com.example.prstamolabctma.model.Equipo
import com.example.prstamolabctma.model.SolicitudPrestamo

fun EquipoDto.toEntity(): EquipoEntity {
    return EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        estado = estado
    )
}

fun EquipoDto.toDomain(): Equipo {
    return toEntity().toDomain()
}

fun SolicitudDto.toEntity(): SolicitudEntity {
    return SolicitudEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        fechaCreacion = fechaCreacion
    )
}

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
